import pandas as pd
from wordcloud import WordCloud
from collections import Counter
from konlpy.tag import Okt
from sqlalchemy import create_engine
import io
import base64

# 불용어 리스트
stopwords = ['투자', '주가', '주식', '가능성', '거래', '보고', '수익', '오늘', '사람', '시장',
             '올해', '내년', '국내', '해외', '기업', '실적', '성장', '매출', '영업', '기대',
             '손실', '차트', '분석', '정보', '데이터', '종목', '변동', '영향', '변화', '예측']

# Okt 형태소 분석기
okt = Okt()

# Oracle DB 연결 설정
db_user = "c##PROJECT"
db_password = "k5002"
db_host = "localhost"
db_port = "1521"
db_service = "xe"

engine = create_engine(f"oracle+cx_oracle://{db_user}:{db_password}@{db_host}:{db_port}/?service_name={db_service}")

def get_wordcloud_image():
    try:
        # 🔹 DB에서 뉴스와 커뮤니티 토큰 데이터 가져오기
        query = """
            SELECT TO_CHAR(NEWS_TOKEN) AS NEWS_TOKEN FROM NEWS
            UNION ALL
            SELECT TO_CHAR(CONTENT_TOKEN) AS CONTENT_TOKEN FROM COMMUNITY
        """
        df = pd.read_sql(query, con=engine)

        # 결측값 제거 후 텍스트 데이터 결합
        df = df.dropna()
        text_data = ' '.join(df.iloc[:, 0])

        # 명사 추출 함수
        def extract_nouns(text):
            nouns = okt.nouns(text)  # 명사 추출
            return ' '.join(nouns)  # 공백으로 연결하여 반환

        # 명사 추출 및 불용어 제거
        text_data = extract_nouns(text_data)
        words = text_data.split()
        filtered_words = [word for word in words if word not in stopwords]
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
            min_word_length=2,
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