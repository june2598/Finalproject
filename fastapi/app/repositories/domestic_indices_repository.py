import cx_Oracle

dsn = cx_Oracle.makedsn('localhost', 1521, service_name='xe')

def get_db_connection():
    """Oracle DB 연결을 반환하는 함수"""
    return cx_Oracle.connect(user='c##PROJECT', password='k5002', dsn=dsn)

def insert_domestic_index(market_id, index_value, index_comp, change_ratio, cdate):
    """DOMESTIC_INDICES 테이블에 시장 지수 데이터 삽입"""
    with get_db_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute("""
                INSERT INTO DOMESTIC_INDICES (DOMESTIC_INDICES_ID, MARKET_ID, INDEX_VALUE, INDEX_COMP, CHANGE_RATIO, CDATE)
                VALUES (DOMESTIC_INDICES_SEQ.NEXTVAL, :market_id, :index_value, :index_comp, :change_ratio, :cdate)
            """, {
                'market_id': market_id,
                'index_value': index_value,
                'index_comp': index_comp,
                'change_ratio': change_ratio,
                'cdate': cdate
            })
            conn.commit()
