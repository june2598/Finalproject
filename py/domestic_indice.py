import FinanceDataReader as fdr
import cx_Oracle
import pandas as pd
from datetime import datetime

# 오늘 날짜 가져오기
today = datetime.now().strftime('%Y-%m-%d')

today_kospi_indices = fdr.DataReader('KS11', today)[['Close','Comp','Change']]
today_kosdaq_indices = fdr.DataReader('KQ11',today)[['Close','Comp','Change']]

# 데이터베이스 연결
dsn = cx_Oracle.makedsn('localhost', 1521, service_name='xe')
connection = cx_Oracle.connect(user='c##PROJECT', password='k5002', dsn=dsn)
cursor = connection.cursor()

# KOSPI 데이터 삽입
for index, row in today_kospi_indices.iterrows():
    change_ratio = round(row['Change'] * 100, 2)  # Change를 100배하고 소수점 둘째 자리까지 반올림
    cursor.execute("""
        INSERT INTO DOMESTIC_INDICES (DOMESTIC_INDICES_ID, MARKET_ID, INDEX_VALUE, INDEX_COMP, CHANGE_RATIO, CDATE)
        VALUES (DOMESTIC_INDICES_SEQ.NEXTVAL, :market_id, :index_value, :index_comp, :change_ratio, :cdate)
    """, {
        'market_id': 1,
        'index_value': row['Close'],
        'index_comp': row['Comp'],
        'change_ratio': change_ratio,
        'cdate': index
    })

# KOSDAQ 데이터 삽입
for index, row in today_kosdaq_indices.iterrows():
    change_ratio = round(row['Change'] * 100, 2)  # Change를 100배하고 소수점 둘째 자리까지 반올림
    cursor.execute("""
        INSERT INTO DOMESTIC_INDICES (DOMESTIC_INDICES_ID, MARKET_ID, INDEX_VALUE, INDEX_COMP, CHANGE_RATIO, CDATE)
        VALUES (DOMESTIC_INDICES_SEQ.NEXTVAL, :market_id, :index_value, :index_comp, :change_ratio, :cdate)
    """, {
        'market_id': 2,
        'index_value': row['Close'],
        'index_comp': row['Comp'],
        'change_ratio': change_ratio,
        'cdate': index
    })
    
    
# 변경 사항 커밋 및 연결 종료
connection.commit()
cursor.close()
connection.close()    
 
 