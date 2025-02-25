import cx_Oracle
import pandas as pd

dsn = cx_Oracle.makedsn('localhost', 1521, service_name='xe')

def get_db_connection() :
  return cx_Oracle.connect(user='c##PROJECT',password='k5002',dsn=dsn)


  # SECTORS 테이블에서 기존 업종 데이터를 가져옴
def get_existing_sectors():
    with get_db_connection() as conn:
      with conn.cursor() as cursor:
        cursor.execute("SELECT SEC_ID, SEC_NM FROM SECTORS")
        return {row[1]: row[0] for row in cursor.fetchall()} # {'업종명': SEC_ID} 형태 반환

# SECTORS 테이블에 새로운 업종 추가
def insert_new_sector(sector_name, sector_code, market_id):

  print(f"ECTORS INSERT 요청: sector={sector_name}, sector_code={sector_code}, market_id={market_id}")  # 로그 확인

  with get_db_connection() as conn:
    with conn.cursor() as cursor:
      cursor.execute("""
          INSERT INTO SECTORS (SEC_ID, SEC_NM, SEC_CODE, MARKET_ID)
          VALUES (SECTORS_SEQ.NEXTVAL, :sector, :sec_code, :market_id)
      """, {'sector': sector_name, 'sec_code':sector_code, 'market_id': market_id})
      conn.commit()

      cursor.execute("SELECT SEC_ID FROM SECTORS WHERE SEC_NM = :sector", {'sector': sector_name})
      return cursor.fetchone()[0] # 새로 추가된 SEC_ID 반환

# STOCKS 테이블에 종목 추가/업데이트
def update_or_insert_stock(code, name, sec_id, market_id):
  with get_db_connection() as conn:
    with conn.cursor() as cursor:
      cursor.execute("SELECT STK_ID FROM STOCKS WHERE STK_CODE = :stk_code", {'stk_code': code})
      result = cursor.fetchone()

      if result:  # 이미 존재하면 업데이트
        cursor.execute("""
                          UPDATE STOCKS SET SEC_ID = :sec_id WHERE STK_CODE = :stk_code
                      """, {'sec_id': sec_id, 'stk_code': code})
      else:  # 없으면 추가
        cursor.execute("""
                          INSERT INTO STOCKS (STK_ID, STK_CODE, STK_NM, SEC_ID, MARKET_ID, CDATE)
                          VALUES (STOCKS_SEQ.NEXTVAL, :stk_code, :stk_nm, :sec_id, :market_id, SYSDATE)
                      """, {'stk_code': code, 'stk_nm': name, 'sec_id': sec_id, 'market_id': market_id})

      conn.commit()