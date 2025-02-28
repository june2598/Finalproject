import re
import datetime
from typing import Optional
from konlpy.tag import Okt

okt = Okt()  # ✅ Okt 형태소 분석기 초기화


# ✅ 날짜 변환 함수 (Oracle Timestamp 형식 변환)
def format_timestamp(timestamp: str) -> Optional[str]:
  try:
    dt = datetime.datetime.fromisoformat(timestamp.replace("Z", "+00:00"))
    return dt.strftime("%y/%m/%d %H:%M:%S.%f")  # Oracle TIMESTAMP 형식
  except ValueError:
    return None  # 변환 실패 시 None 반환


# ✅ 댓글에서 명사 추출 (토큰화)
def extract_nouns(text: str) -> str:
  nouns = okt.nouns(text)  # 명사 추출
  return ' '.join(nouns)  # 공백으로 연결하여 반환


# ✅ 감성 분석을 위한 전처리 (특수문자 및 URL 제거)
def preprocess_text(text: str) -> Optional[str]:
  if re.search(r'http[s]?://|www\.|\.com|\.net', text) or not text.strip():
    return None  # 광고성 또는 빈 문자열은 제외

  text = re.sub(r'[^ㄱ-ㅎㅏ-ㅣ가-힣 ]', '', text)  # 특수 문자 제거
  return text.strip() if text.strip() else None  # 제거 후에도 빈 문자열이면 제외
