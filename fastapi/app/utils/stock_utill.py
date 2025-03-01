# 파일 위치: utils/stock_utils.py
import FinanceDataReader as fdr


def code_list_by_market(market):
  """ KOSPI, KOSDAQ, ETF 시장별 전체 종목 리스트 반환 """
  market_list = ['KOSPI', 'KOSDAQ', 'ETF']

  if market not in market_list:
    raise ValueError(f"[오류] '{market}'는 지원하지 않는 시장입니다. (지원 가능: KOSPI, KOSDAQ, ETF)")

  if market in ['KOSPI', 'KOSDAQ']:
    df = fdr.StockListing(market).sort_values(by='Marcap', ascending=False)
  else:  # ETF 처리 (컬럼명이 다름)
    df = fdr.StockListing('ETF/KR').sort_values(by='MarCap', ascending=False)
    df.rename(columns={'Symbol': 'Code', 'MarCap': 'Marcap'}, inplace=True)

  return df[['Code', 'Name']]  # 전체 종목 반환
