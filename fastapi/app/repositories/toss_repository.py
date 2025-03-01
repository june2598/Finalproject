# 📌 파일 위치: repository/toss_repository.py
import requests
import time


def get_subject_id(stock_code):
  """
  Toss Invest API에서 종목 코드(A005930 → subjectId) 변환
  """
  url = f"https://wts-info-api.tossinvest.com/api/v1/stock-detail/ui/A{stock_code}/common"
  headers = {"User-Agent": "Mozilla/5.0", "Content-Type": "application/json"}

  for attempt in range(3):  # 최대 3번 재시도
    try:
      response = requests.get(url, headers=headers, timeout=10)
      if response.status_code == 200:
        data = response.json()
        return stock_code, data.get("result", {}).get("guid")  # (종목코드, subjectId) 반환
    except requests.exceptions.RequestException:
      time.sleep(2)  # 2초 대기 후 재시도
  return stock_code, None  # subjectId 조회 실패 시 None 반환
