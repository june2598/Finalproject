import datetime
import pandas as pd
import FinanceDataReader as fdr
from dateutil.relativedelta import relativedelta
from app.repositories.rec_stk_update_repository import get_stk_id_mapping, insert_stock_data

def calculate_start_date(months_ago, end_date):
    """ n개월 전 날짜 계산 """
    start_date = datetime.datetime.strptime(end_date, '%Y-%m-%d') - relativedelta(months=months_ago)
    return start_date.strftime('%Y-%m-%d')

def fetch_stock_data():
    """ 주식 데이터 수집 후 데이터베이스 저장 """
    today_str = datetime.datetime.today().strftime('%Y-%m-%d')

    # 🔹 KOSPI, KOSDAQ, ETF 리스트 가져오기
    kospi = fdr.StockListing('KOSPI')
    kosdaq = fdr.StockListing('KOSDAQ')
    etfs = fdr.StockListing('ETF/KR')

    # 🔹 종목 코드 리스트 생성
    all_stock_codes = kospi['Code'].to_list() + kosdaq['Code'].to_list() + etfs['Symbol'].to_list()

    all_data = []
    failed_codes = []

    for code in all_stock_codes:
        try:
            close_df = fdr.DataReader(code, today_str)[['Close']].reset_index()
            close_df['Code'] = code
            all_data.append(close_df)
        except Exception:
            failed_codes.append(code)

    if failed_codes:
        print("조회 실패한 종목 코드:", failed_codes)

    # 데이터 병합
    result_df = pd.concat(all_data, ignore_index=True)

    # STK_CODE → STK_ID 매핑
    stk_code_to_id = get_stk_id_mapping()

    # Date 형식 변환
    result_df["TRADE_DATE"] = pd.to_datetime(result_df["Date"]).dt.strftime("%y/%m/%d")

    # STK_ID 매핑 적용
    insert_data = []
    for _, row in result_df.iterrows():
        stk_id = stk_code_to_id.get(row["Code"])
        if stk_id:
            insert_data.append((stk_id, row["Close"], row["TRADE_DATE"]))

    # DB 저장 실행
    insert_stock_data(insert_data)

    return {
        "success": True,
        "message": f"{len(insert_data)}개의 주식 데이터 업데이트 완료",
        "failed_codes": failed_codes
    }
