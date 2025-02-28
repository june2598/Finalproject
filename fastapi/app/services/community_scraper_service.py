import requests
import pandas as pd
import time
import concurrent.futures
from datetime import datetime, timedelta
from tqdm import tqdm
from konlpy.tag import Okt
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
import pickle
import json
from app.repositories.community_repository import CommunityRepository


class ScraperService:
  def __init__(self):
    self.okt = Okt()
    self.repository = CommunityRepository()

    # 감성 분석 모델 및 토크나이저 로드
    save_dir = 'saved_models'
    self.model = load_model(f"{save_dir}/best_model.keras")
    with open(f"{save_dir}/tokenizer.pkl", 'rb') as handle:
      self.tokenizer = pickle.load(handle)
    with open(f"{save_dir}/preprocessing_info.json", 'r') as json_file:
      self.preprocessing_info = json.load(json_file)
    self.max_len = self.preprocessing_info['max_len']

  def extract_nouns(self, text):
    nouns = self.okt.nouns(text)
    return ' '.join(nouns)

  def predict_sentiment(self, text):
    encoded = self.tokenizer.texts_to_sequences([text])
    pad_new = pad_sequences(encoded, maxlen=self.max_len)
    score = float(self.model.predict(pad_new)[0])
    return 1 if score > 0.5 else 0

  def fetch_comments(self, stock_code, subject_id, start_date, end_date):
    url = "https://wts-cert-api.tossinvest.com/api/v3/comments"
    headers = {"User-Agent": "Mozilla/5.0"}

    comments_list = []
    last_comment_id = None
    start_date = pd.to_datetime(start_date).date()
    end_date = pd.to_datetime(end_date).date()
    request_count = 0

    with tqdm(total=100, desc=f"📡 {stock_code} 댓글 크롤링", leave=False) as pbar:
      while request_count < 100:
        payload = {"commentSortType": "RECENT", "subjectId": subject_id, "subjectType": "STOCK"}
        if last_comment_id:
          payload["commentId"] = last_comment_id

        response = requests.post(url, json=payload, headers=headers, timeout=10)
        if response.status_code != 200:
          break

        data = response.json().get("result", {}).get("comments", {}).get("body", [])
        if not data:
          break

        for comment in data:
          comment_date = pd.to_datetime(comment["updatedAt"]).date()
          if comment_date > end_date:
            continue
          if comment_date < start_date:
            break

          comments_list.append({
            "종목코드": stock_code,
            "subjectId": subject_id,
            "댓글": comment["message"],
            "날짜": comment["updatedAt"]
          })

        last_comment_id = data[-1]["id"]
        request_count += 1
        pbar.update(1)
        time.sleep(1)

    return comments_list

  def run_scraper(self, start_date, end_date):
    markets = ["KOSPI", "KOSDAQ", "ETF"]
    all_data = []

    for market in markets:
      stock_df = code_list_by_market(market)
      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        subject_ids = dict(tqdm(executor.map(get_subject_id, stock_df["Code"]), total=len(stock_df)))

      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        results = executor.map(lambda args: self.fetch_comments(*args, start_date, end_date), subject_ids.items())

      for result in results:
        all_data.extend(result)

    df = pd.DataFrame(all_data)
    df["댓글_토큰"] = df["댓글"].apply(self.extract_nouns)
    df["긍정라벨"] = df["댓글"].apply(self.predict_sentiment)

    for _, row in df.iterrows():
      self.repository.save_comment(row["종목코드"], row["댓글"], row["긍정라벨"], row["날짜"])

    return {"message": "✅ 데이터 수집 및 저장 완료!", "count": len(df)}
