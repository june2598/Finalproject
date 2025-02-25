import cx_Oracle
import pandas as pd

# 오라클 DB 연결 정보
dsn = cx_Oracle.makedsn("localhost", 1521, service_name="xe")

def get_db_connection():
    """ 데이터베이스 연결 """
    return cx_Oracle.connect(user="c##PROJECT", password="k5002", dsn=dsn)

def get_stk_id_mapping():
    """ STK_CODE → STK_ID 매핑 정보 조회 """
    with get_db_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute("SELECT STK_CODE, STK_ID FROM MKT_SEC_STK")
            return {stk_code: stk_id for stk_code, stk_id in cursor.fetchall()}

def insert_stock_data(stock_data):
    """ REC_STK 테이블에 주식 데이터 저장 """
    insert_sql = """
    INSERT INTO REC_STK (REC_STK_ID, STK_ID, REC_PRICE, TRADE_DATE, CDATE) 
    VALUES (REC_STK_SEQ.NEXTVAL, :1, :2, TO_DATE(:3, 'YY/MM/DD'), SYSDATE)
    """

    with get_db_connection() as conn:
        with conn.cursor() as cursor:
            cursor.executemany(insert_sql, stock_data)
            conn.commit()
