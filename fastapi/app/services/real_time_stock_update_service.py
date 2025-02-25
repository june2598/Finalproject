import pandas as pd
import FinanceDataReader as fdr
from app.repositories.real_time_stock_update_repository import get_stock_id_mapping, insert_rt_stk_data

def process_market_data(market_type, stock_code_col, price_col, change_col, change_ratio_col, volume_col, amount_col, marcap_col, include_market=True):
    """
    주어진 시장 데이터를 가져와 STOCKS 테이블에서 STK_ID를 조회한 후, RT_STK 테이블에 저장하는 서비스 함수
    """
    # ✅ 1️⃣ 시장 데이터 불러오기
    selected_columns = [stock_code_col, 'Name', price_col, change_col, change_ratio_col, volume_col, amount_col, marcap_col]
    if include_market:
        selected_columns.insert(2, 'Market')  # KOSPI, KOSDAQ 데이터에만 Market 포함

    market_list = fdr.StockListing(market_type)[selected_columns]

    # ✅ 2️⃣ 데이터 타입 변환
    numeric_cols = [price_col, change_col, change_ratio_col, volume_col, amount_col, marcap_col]
    for col in numeric_cols:
        market_list[col] = pd.to_numeric(market_list[col], errors='coerce').fillna(0).astype(float)

    # ✅ 3️⃣ ETF 시장 변환
    if market_type == 'ETF/KR':
        market_list[amount_col] *= 1_000_000  # 100만 곱하기
        market_list[marcap_col] *= 100_000_000  # 1억 곱하기

    # ✅ 4️⃣ STOCKS 테이블에서 STK_ID 조회
    stock_id_mapping, missing_codes = get_stock_id_mapping(market_list[stock_code_col].unique())

    if missing_codes:
        print(f"⚠️ STOCKS 테이블에 없는 종목 코드: {missing_codes}")

    # ✅ 5️⃣ STK_ID 매핑 및 데이터 정리
    market_list['STK_ID'] = market_list[stock_code_col].map(stock_id_mapping)
    market_list = market_list.dropna(subset=['STK_ID'])

    # ✅ 6️⃣ 컬럼명 통일
    market_list.rename(columns={
        price_col: "PRICE",
        change_col: "CHANGE",
        change_ratio_col: "CHANGE_RATIO",
        volume_col: "VOLUME",
        amount_col: "AMOUNT",
        marcap_col: "MARCAP"
    }, inplace=True)

    # ✅ 7️⃣ RT_STK 테이블에 데이터 저장
    insert_rt_stk_data(market_list)
    return {"success": True, "message": f"{market_type} 데이터 업데이트 완료!"}
