import cx_Oracle

# ✅ Oracle DSN 설정
dsn = cx_Oracle.makedsn('localhost', 1521, service_name='xe')

# ✅ Oracle 연결 풀 생성 (최대 10개까지 연결 유지)
pool = cx_Oracle.SessionPool(user='c##PROJECT', password='k5002', dsn=dsn,
                             min=2, max=10, increment=1, threaded=True)

def get_db_connection():
    """Oracle DB 연결을 반환하는 함수 (연결 풀 사용)"""
    return pool.acquire()
