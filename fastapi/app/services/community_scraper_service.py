import os
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
from app.repositories.toss_repository import get_subject_id
from app.utils.stock_utill import code_list_by_market
from dateutil import parser

class ScraperService:
  def __init__(self):
    """초기화: 감성 분석 모델 및 토크나이저 로드"""
    self.okt = Okt()
    self.repository = CommunityRepository()

    # 프로젝트 루트 디렉토리 찾기 (절대 경로 적용)
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    MODEL_DIR = os.path.join(BASE_DIR, "saved_models")

    # 모델, 토크나이저, 전처리 정보 경로 지정
    MODEL_PATH = os.path.join(MODEL_DIR, "best_model.keras")
    TOKENIZER_PATH = os.path.join(MODEL_DIR, "tokenizer.pkl")
    PREPROCESSING_INFO_PATH = os.path.join(MODEL_DIR, "preprocessing_info.json")

    print(f"📂 모델 경로: {MODEL_PATH}")  # ✅ 디버깅용 출력

    # 모델 로드 (파일 존재 여부 확인 후 로드)
    if not os.path.exists(MODEL_PATH):
      raise FileNotFoundError(f"모델 파일이 존재하지 않습니다: {MODEL_PATH}")
    self.model = load_model(MODEL_PATH)

    # 토크나이저 로드
    if not os.path.exists(TOKENIZER_PATH):
      raise FileNotFoundError(f"토크나이저 파일이 존재하지 않습니다: {TOKENIZER_PATH}")
    with open(TOKENIZER_PATH, 'rb') as handle:
      self.tokenizer = pickle.load(handle)

    # ✅ 전처리 정보 로드
    if not os.path.exists(PREPROCESSING_INFO_PATH):
      raise FileNotFoundError(f"전처리 정보 파일이 존재하지 않습니다: {PREPROCESSING_INFO_PATH}")
    with open(PREPROCESSING_INFO_PATH, 'r') as json_file:
      self.preprocessing_info = json.load(json_file)

    self.max_len = self.preprocessing_info['max_len']

  def format_timestamp(self, timestamp: str) -> str:
    """ISO 8601 날짜를 Oracle TIMESTAMP 형식으로 변환"""
    try:
      dt = parser.isoparse(timestamp)  # ✅ ISO 8601 → datetime 객체 변환
      return dt.strftime("%Y-%m-%d %H:%M:%S.%f")  # ✅ Oracle TIMESTAMP 형식 변환
    except Exception as e:
      print(f"[오류] 날짜 변환 실패: {timestamp}, {e}")
      return None  # 변환 실패 시 None 반환


  def extract_nouns(self, text):
    """명사 추출"""
    nouns = self.okt.nouns(text)
    return ' '.join(nouns)

  def predict_sentiment_batch(self, texts):
    """배치 단위 감성 분석 (속도 최적화)"""
    encoded = self.tokenizer.texts_to_sequences(texts)
    pad_new = pad_sequences(encoded, maxlen=self.max_len)
    scores = self.model.predict(pad_new)
    return [1 if score > 0.5 else 0 for score in scores]

  def fetch_comments(self, stock_code, subject_id, start_date, end_date):
    """Toss Invest 댓글 크롤링"""
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
            print(f"[{stock_code}] {start_date} 이전 댓글 발견 -> 다음 종목 이동")
            return comments_list  # 즉시 반환 (추가 요청 방지)

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
    """모든 시장(KOSPI, KOSDAQ, ETF) 크롤링"""
    markets = ["KOSPI", "KOSDAQ", "ETF"]
    all_data = []

    for market in markets:
      stock_df = code_list_by_market(market)

      # ✅ 멀티스레딩으로 subjectId 가져오기
      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        subject_id_results = list(tqdm(executor.map(get_subject_id, stock_df["Code"]), total=len(stock_df)))

      # ✅ None 값 제거 후 딕셔너리 변환
      subject_ids = {code: sid for code, sid in subject_id_results if sid}

      # ✅ 멀티스레딩으로 댓글 크롤링
      with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        results = executor.map(lambda args: self.fetch_comments(*args, start_date, end_date), subject_ids.items())

      for result in results:
        all_data.extend(result)

    df = pd.DataFrame(all_data)
    df["댓글_토큰"] = df["댓글"].apply(self.extract_nouns)
    df["긍정라벨"] = self.predict_sentiment_batch(df["댓글"].tolist())  # 배치 처리로 속도 개선

    # ✅ DB에 저장
    for _, row in df.iterrows():
      formatted_date = self.format_timestamp(row["날짜"])  # 날짜 변환
      if formatted_date:  # 변환 성공한 경우만 저장
        self.repository.save_comment(row["종목코드"], row["댓글"], row["댓글_토큰"], row["긍정라벨"],formatted_date)

    return {"message": "데이터 수집 및 저장 완료!", "count": len(df)}


