import pandas as pd
from datetime import datetime
import FinanceDataReader as fdr
from app.repositories.domestic_indices_repository import insert_domestic_index

def update_domestic_indices():
    """국내 시장 지수를 가져와 데이터베이스에 저장하는 서비스 함수"""

    # ✅ 1️⃣ 오늘 날짜 가져오기
    today = datetime.now().strftime('%Y-%m-%d')

    # ✅ 2️⃣ KOSPI & KOSDAQ 데이터 가져오기
    today_kospi_indices = fdr.DataReader('KS11', today)[['Close', 'Comp', 'Change']]
    today_kosdaq_indices = fdr.DataReader('KQ11', today)[['Close', 'Comp', 'Change']]

    # ✅ 3️⃣ 데이터 삽입 (KOSPI: market_id=1, KOSDAQ: market_id=2)
    for index, row in today_kospi_indices.iterrows():
        change_ratio = round(row['Change'] * 100, 2)  # Change를 100배하고 소수점 둘째 자리까지 반올림
        insert_domestic_index(1, row['Close'], row['Comp'], change_ratio, index)

    for index, row in today_kosdaq_indices.iterrows():
        change_ratio = round(row['Change'] * 100, 2)
        insert_domestic_index(2, row['Close'], row['Comp'], change_ratio, index)

    return {"success": True, "message": "국내 시장 지수 업데이트 완료!"}
