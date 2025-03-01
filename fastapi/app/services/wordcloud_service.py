# services 계층 : 데이터 가공 및 비즈니스 로직
import os
import json
import io
import base64
from collections import Counter
from wordcloud import WordCloud
from konlpy.tag import Okt
from app.repositories.wordcloud_repository import fetch_tokens_from_db

# 불용어 리스트 로드

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
STOPWORDS_PATH = os.path.join(BASE_DIR, "config", "stopwords.json")

def load_stopwords():
    try:
        with open(STOPWORDS_PATH, "r", encoding="utf-8") as f:
            return set(json.load(f))  # 리스트를 set으로 변환하여 검색 속도 최적화
    except FileNotFoundError:
        print(f"⚠️ [경고] '{STOPWORDS_PATH}' 파일을 찾을 수 없습니다. 기본 불용어 리스트 사용")
        return {"투자", "주가", "주식", "가능성", "거래", "보고", "수익", "오늘", "사람", "시장", "올해", "내년", "국내", "해외", "기업", "실적", "성장",
                "매출", "영업", "기대", "손실", "차트", "분석", "정보", "데이터", "종목", "변동", "영향", "변화", "예측"}  # 기본 불용어 리스트

stopwords = load_stopwords()

# Okt 형태소 분석기
okt = Okt()

# 명사 추출 함수
def extract_nouns(text):
    nouns = okt.nouns(text)  # 명사 추출
    return ' '.join(nouns)  # 공백으로 연결하여 반환

def generate_wordcloud():
    try:
        df = fetch_tokens_from_db()
        text_data = ' '.join(df.iloc[:, 0])

        # 명사 추출 및 불용어 제거
        text_data = extract_nouns(text_data)
        words = text_data.split()
        filtered_words = [word for word in words if word not in stopwords and len(word) > 1]
        word_counts = Counter(filtered_words)

        # 워드 클라우드 생성
        font_path = r'C:\Windows\Fonts\malgun.ttf'  # 한글 폰트 경로 (Windows 기준)
        wordcloud = WordCloud(
            font_path=font_path,
            width=300, height=300,
            background_color='white',
            max_words=50,
            min_font_size=10,
            max_font_size=50,
            random_state=2024,
        ).generate_from_frequencies(word_counts)

        # 이미지를 메모리에 저장
        img = io.BytesIO()
        wordcloud.to_image().save(img, format='PNG')
        img.seek(0)

        # Base64로 인코딩 후 반환
        img_base64 = base64.b64encode(img.getvalue()).decode('utf-8')
        return {"image": f"data:image/png;base64,{img_base64}"}

    except Exception as e:
        return {"error": str(e)}