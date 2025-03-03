import requests
from bs4 import BeautifulSoup
import pandas as pd
import datetime as dt
import random
import time
import torch
import os
from tqdm import tqdm
from transformers import BertForSequenceClassification, AutoTokenizer
from konlpy.tag import Okt
from concurrent.futures import ThreadPoolExecutor, as_completed
from app.repositories.news_repository import insert_news
import hashlib

# 환경 변수 설정 (Windows 환경에서 symlink 문제 해결)
os.environ["HUGGINGFACE_HUB_DISABLE_SYMLINKS_WARNING"] = "1"

# KoBERT 모델 경로 설정
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
KOBERT_PATH = os.path.join(BASE_DIR, "kobert")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# KoBERT 모델, 토크나이저 로드
model = BertForSequenceClassification.from_pretrained(KOBERT_PATH, trust_remote_code=True).to(device)
tokenizer = AutoTokenizer.from_pretrained(KOBERT_PATH, trust_remote_code=True)

# 형태소 분석기
okt = Okt()

# User-Agent 랜덤 사용
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36",
    "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1",
    "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1",
]

# 현재 시간
now = dt.datetime.now()

# start_time: 어제 오전 9시
start_time = now.replace(hour=9, minute=0, second=0, microsecond=0) - dt.timedelta(days=1)

# end_time: 오늘 오전 9시
end_time = now.replace(hour=9, minute=0, second=0, microsecond=0)

# 명사 추출 함수 (토큰화)
def extract_nouns(text):
    """기사 본문에서 명사 추출"""
    nouns = okt.nouns(text)
    return ' '.join(nouns)

# 종목 코드 리스트 가져오기 (KOSPI, KOSDAQ, ETF)
def code_list_by_market(market):
    import FinanceDataReader as fdr

    if market in ["KOSPI", "KOSDAQ"]:
        df = fdr.StockListing(market).sort_values(by="Marcap", ascending=False)
    elif market == "ETF":
        df = fdr.StockListing("ETF/KR").sort_values(by="MarCap", ascending=False)
        df.rename(columns={"Symbol": "Code"}, inplace=True)
    else:
        raise ValueError("시장입력오류")
    return df["Code"].tolist()

# 네이버 금융 뉴스 URL을 네이버 뉴스 URL로 변환
def convert_finance_url_to_news_url(finance_url):
    import re
    match = re.search(r"article_id=(\d+)&office_id=(\d+)", finance_url)
    if match:
        article_id, office_id = match.groups()
        return f"https://n.news.naver.com/mnews/article/{office_id}/{article_id}"
    return finance_url

# 특정 종목 뉴스 크롤링 (어제 오전 9시 ~ 현재, 중복 제거)
def get_news_for_stock(code):
    base_url = "https://finance.naver.com/item/news_news.naver"
    page = 1
    news_list = []
    seen_articles = set()

    while page <= 20:
        params = {"code": code, "page": page}
        headers = {
          "User-Agent": random.choice(USER_AGENTS),
          "Referer": f"https://finance.naver.com/item/news_news.naver?code={code}",
        }

        try:
            response = requests.get(base_url, params=params, headers=headers, timeout=5)
            soup = BeautifulSoup(response.text, "html.parser")
            news_rows = soup.select("table.type5 tr")

            if not news_rows:
                break

            for row in news_rows:
                title_tag = row.select_one("td.title a")
                if not title_tag:
                    continue
                title = title_tag.text.strip()
                link = "https://finance.naver.com" + title_tag["href"]

                if (title, link) in seen_articles:
                    continue
                seen_articles.add((title, link))

                source_tag = row.select_one("td.info")
                source = source_tag.text.strip() if source_tag else "정보 없음"

                date_tag = row.select_one("td.date")
                date_str = date_tag.text.strip() if date_tag else None

                news_time = dt.datetime.strptime(date_str, "%Y.%m.%d %H:%M")

                if news_time < start_time:
                    return news_list  # 최신 뉴스만 유지

                news_list.append({
                    "code": code, "title": title, "link": link,
                    "source": source, "date": news_time, "content": ""
                })

            page += 1
            time.sleep(random.uniform(1.0, 3.0))

        except requests.RequestException:
            break

    return news_list



# 중복 기사 필터링을 위한 해시값 저장 (종목 코드별로 관리)
seen_content_hashes = {}

def get_news_content(news_url, code):
    headers = {
        "User-Agent": random.choice(USER_AGENTS),
        "Referer": "https://finance.naver.com/"
    }
    news_url = convert_finance_url_to_news_url(news_url)

    try:
        response = requests.get(news_url, headers=headers, timeout=5)
        soup = BeautifulSoup(response.text, "html.parser")
        content_tag = soup.select_one("#dic_area") or soup.select_one(".article_body_contents")
        content = content_tag.get_text(strip=True) if content_tag else "본문 없음"

        # 기사 내용과 종목 코드를 함께 해시값 계산
        content_hash = hashlib.md5((content + code).encode("utf-8")).hexdigest()

        # 종목 코드별로 해시값 관리
        if code not in seen_content_hashes:
            seen_content_hashes[code] = set()

        # 중복 기사 확인
        if content_hash in seen_content_hashes[code]:
            return None  # 중복 기사는 None 반환

        seen_content_hashes[code].add(content_hash)
        return content

    except Exception:
        return "본문 크롤링 실패"


# 감성 분석 수행
def batch_predict_sentiment(texts, batch_size=64):
    results = []
    for i in tqdm(range(0, len(texts), batch_size), desc="감성 분석 진행", unit="batch"):
        batch = texts[i : i + batch_size]
        inputs = tokenizer(batch, return_tensors="pt", truncation=True, padding="max_length", max_length=512).to(device)

        with torch.no_grad():
            outputs = model(**inputs)
            probs = torch.nn.functional.softmax(outputs.logits, dim=-1)
            labels = torch.argmax(probs, dim=-1).cpu().numpy()

        results.extend(labels)
    return results

# 전체 뉴스 크롤링 & DB 저장
def crawl_and_save_news():
    markets = ["KOSPI", "KOSDAQ", "ETF"]
    all_news = []
    max_workers = 5

    for market in markets:
        codes = code_list_by_market(market)

        with ThreadPoolExecutor(max_workers=max_workers) as executor, tqdm(total=len(codes), desc=f"{market} 뉴스 크롤링") as pbar:
            future_to_code = {executor.submit(get_news_for_stock, code): code for code in codes}

            for future in as_completed(future_to_code):
                stock_news = future.result()
                all_news.extend(stock_news)
                pbar.update(1)

    with ThreadPoolExecutor(max_workers=max_workers) as executor, tqdm(total=len(all_news), desc="본문 크롤링 진행") as pbar:
        future_to_index = {executor.submit(get_news_content, news["link"], news["code"]): idx for idx, news in enumerate(all_news)}

        for future in as_completed(future_to_index):
            index = future_to_index[future]
            content = future.result()

            if content is None:  # 중복 기사는 건너뜀
                all_news[index]["content"] = "중복 기사"
            else:
                all_news[index]["content"] = content
            pbar.update(1)

    df = pd.DataFrame(all_news)

    # 감성 분석 수행
    df["news_pos_label"] = batch_predict_sentiment(df["content"].tolist())

    # 명사 추출 (토큰화)
    df["news_token"] = df["content"].apply(extract_nouns)

    for _, row in df.iterrows():
        news_data = row.to_dict()
        print(type(news_data["date"]))
        print("news_data:", news_data)  # 딕셔너리 출력
        insert_news(row.to_dict())  # DB 저장

    return {"message": "뉴스 크롤링 완료", "news_count": len(df)}
