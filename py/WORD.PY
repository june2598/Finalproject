import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from wordcloud import WordCloud
from collections import Counter
from konlpy.tag import Okt
import cx_Oracle
from sqlalchemy import create_engine
import os

okt = Okt()

stopwords = ['투자', '주가', '주식', '가능성', '거래', '보고', '수익', '오늘', '사람', '시장',
             '올해', '내년', '국내', '해외', '기업', '실적', '성장', '매출', '영업', '기대',
             '손실', '차트', '분석', '정보', '데이터', '종목', '변동', '영향', '변화', '예측']

# 🔹 Oracle DB 연결 (SQLAlchemy 사용)
db_user = "c##PROJECT"
db_password = "k5002"
db_host = "localhost"
db_port = "1521"
db_service = "xe"

engine = create_engine(f"oracle+cx_oracle://{db_user}:{db_password}@{db_host}:{db_port}/?service_name={db_service}")

# 🔹 DB에서 뉴스와 커뮤니티 토큰 데이터 가져오기
query = """
    SELECT NEWS_TOKEN FROM NEWS
    UNION ALL
    SELECT CONTENT_TOKEN FROM COMMUNITY
"""
df = pd.read_sql(query, con=engine)

df = df.dropna()
text_data = ' '.join(df.iloc[:, 0]) 

# 🔹 명사 추출 함수
def extract_nouns(text):
    nouns = okt.nouns(text)  # 명사 추출
    return ' '.join(nouns)  # 공백으로 연결하여 반환

# 🔹 명사 추출 및 불용어 제거
text_data = extract_nouns(text_data)
words = text_data.split()
filtered_words = [word for word in words if word not in stopwords]
word_counts = Counter(filtered_words)

# 🔹 워드 클라우드 생성
font_path = r'C:\Windows\Fonts\malgun.ttf'  # 한글 폰트 경로 (Windows 기준)
wordcloud = WordCloud(
    font_path=font_path,
    width=300, height=300,
    background_color='white',
    max_words=50,
    min_font_size=10, 
    max_font_size=50, 
    min_word_length=2,
    random_state=2024,
).generate_from_frequencies(word_counts)