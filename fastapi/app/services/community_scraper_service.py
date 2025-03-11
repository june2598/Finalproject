import os
import requests
import pandas as pd
import time
import random
import concurrent.futures
from datetime import datetime
from tqdm import tqdm
from konlpy.tag import Okt
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
import pickle
import json
from tenacity import retry, stop_after_attempt, wait_exponential
from app.repositories.community_repository import CommunityRepository
from app.repositories.toss_repository import get_subject_id
from app.utils.stock_utill import code_list_by_market
from dateutil import parser


class ScraperService:
  def __init__(self):
    self.okt = Okt()
    self.repository = CommunityRepository()
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    MODEL_DIR = os.path.join(BASE_DIR, "saved_models")

    self.model = load_model(os.path.join(MODEL_DIR, "best_model.keras"))
    with open(os.path.join(MODEL_DIR, "tokenizer.pkl"), 'rb') as handle:
      self.tokenizer = pickle.load(handle)
    with open(os.path.join(MODEL_DIR, "preprocessing_info.json"), 'r') as json_file:
      self.preprocessing_info = json.load(json_file)

    self.max_len = self.preprocessing_info['max_len']
    self.session = requests.Session()
    self.session.headers.update({"User-Agent": "Mozilla/5.0"})

  def format_timestamp(self, timestamp: str) -> str:
    try:
      dt = parser.isoparse(timestamp)
      return dt.strftime("%Y-%m-%d %H:%M:%S.%f")
    except Exception:
      return None

  def extract_nouns(self, text):
    return ' '.join(self.okt.nouns(text))

  def predict_sentiment_batch(self, texts):
    encoded = self.tokenizer.texts_to_sequences(texts)
    pad_new = pad_sequences(encoded, maxlen=self.max_len)
    scores = self.model.predict(pad_new)
    return [1 if score > 0.5 else 0 for score in scores]

  @retry(stop=stop_after_attempt(3), wait=wait_exponential(multiplier=2, min=2, max=10))
  def fetch_comments(self, stock_code, subject_id, start_date, end_date):
    url = "https://wts-cert-api.tossinvest.com/api/v3/comments"
    comments_list = []
    last_comment_id = None

    # start_date와 end_date를 datetime.date 객체로 변환
    start_date = parser.parse(start_date).date() if isinstance(start_date, str) else start_date
    end_date = parser.parse(end_date).date() if isinstance(end_date, str) else end_date

    for _ in tqdm(range(100), desc=f"{stock_code} 댓글 크롤링", leave=False):
      payload = {"commentSortType": "RECENT", "subjectId": subject_id, "subjectType": "STOCK"}
      if last_comment_id:
        payload["commentId"] = last_comment_id

      response = self.session.post(url, json=payload, timeout=10)
      if response.status_code != 200:
        break

      data = response.json().get("result", {}).get("comments", {}).get("body", [])
      if not data:
        break

      for comment in data:
        comment_date = pd.to_datetime(comment["updatedAt"]).date()
        if comment_date < start_date:
          return comments_list  # 즉시 반환
        comments_list.append({
          "종목코드": stock_code,
          "subjectId": subject_id,
          "댓글": comment["message"],
          "날짜": comment["updatedAt"]
        })

      last_comment_id = data[-1]["id"]
      time.sleep(random.uniform(1, 3))  # 차단 방지 대기

    return comments_list

  def run_scraper(self, start_date, end_date):
    markets = ["KOSPI", "KOSDAQ", "ETF"]
    all_data = []

    for market in markets:
      stock_df = code_list_by_market(market)
      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        subject_ids = {code: sid for code, sid in executor.map(get_subject_id, stock_df["Code"]) if sid}

      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        results = executor.map(lambda args: self.fetch_comments(*args, start_date, end_date), subject_ids.items())

      for result in results:
        all_data.extend(result)

      if len(all_data) >= 10000:
        self.save_to_db(all_data)
        all_data.clear()

    self.save_to_db(all_data)
    return {"success": True, "message": "데이터 수집 및 저장 완료!", "count": len(all_data)}

  def save_to_db(self, data):
    df = pd.DataFrame(data)
    df["댓글_토큰"] = df["댓글"].apply(self.extract_nouns)
    df["긍정라벨"] = self.predict_sentiment_batch(df["댓글"].tolist())

    for _, row in df.iterrows():
      formatted_date = self.format_timestamp(row["날짜"])
      if formatted_date:
        self.repository.save_comment(row["종목코드"], row["댓글"], row["댓글_토큰"], row["긍정라벨"], formatted_date)

    return {"message": "데이터 수집 및 저장 완료!", "count": len(df)}


