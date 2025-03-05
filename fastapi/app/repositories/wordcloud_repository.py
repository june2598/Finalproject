from sqlalchemy import create_engine
import pandas as pd

# Oracle DB 연결 설정
db_user = "c##PROJECT"
db_password = "k5002"
db_host = "localhost"
db_port = "1521"
db_service = "xe"

engine = create_engine(f"oracle+cx_oracle://{db_user}:{db_password}@{db_host}:{db_port}/?service_name={db_service}")

def fetch_tokens_from_db():
    """
    데이터베이스에서 뉴스 및 커뮤니티 토큰 데이터를 가져옴
    """
    query = """
        SELECT NEWS_TOKEN AS TOKEN
        FROM NEWS 
        WHERE PUBLISHED_DATE >= SYSDATE - 2
        UNION ALL
        SELECT CONTENT_TOKEN AS TOKEN
        FROM COMMUNITY 
        WHERE POST_DATE >= SYSDATE - 2
    """
    with engine.connect() as connection:
        df = pd.read_sql(query, con=connection)
    return df.dropna()    # 뉴스-토큰과 커뮤니티-토큰을 합친 데이터 프레임을 반환