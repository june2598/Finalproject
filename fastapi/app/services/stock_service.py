import requests
import pandas as pd
import re
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from bs4 import BeautifulSoup
import FinanceDataReader as fdr
from app.repositories.stock_repository import (
    get_existing_sectors, insert_new_sector, update_or_insert_stock
)

# 네이버 금융에서 업종 데이터 크롤링
def fetch_sector_data():
    chrome_option = Options()
    chrome_option.add_experimental_option('detach', True)
    driver = webdriver.Chrome(options=chrome_option)
    url = 'https://finance.naver.com/sise/sise_group.naver?type=upjong'
    driver.get(url)
    driver.implicitly_wait(2)

    rows = driver.find_elements(By.CSS_SELECTOR, '.type_1 > tbody:nth-child(3) > tr')
    sector_data = []

    for row in rows:
        cols = row.find_elements(By.TAG_NAME, 'td')
        if len(cols) >= 6:
            sector_link = cols[0].find_element(By.TAG_NAME, 'a').get_attribute('href')
            match = re.search(r'no=(\d+)', sector_link)
            sector_code = match.group(1) if match else None
            sector_data.append({'업종코드': sector_code, '업종명': cols[0].text.strip()})

    driver.quit()
    return pd.DataFrame(sector_data)


def fetch_stock_data(sector_df):
  """업종별 종목 데이터를 크롤링하여 DataFrame으로 반환"""
  sector_list = sector_df['업종명'].tolist()
  sector_by_stock_list = []

  for sector in sector_list:
    sector_code = sector_df[sector_df['업종명'] == sector]['업종코드'].values[0]
    url = f'https://finance.naver.com/sise/sise_group_detail.naver?type=upjong&no={sector_code}'
    res = requests.get(url)
    soup = BeautifulSoup(res.content, 'lxml')
    rows = soup.select('#contentarea > div:nth-child(5) > table > tbody > tr')

    for tr in rows:
      cols = tr.find_all("td")
      if len(cols) >= 9:
        stock_name = cols[0].text.strip().replace('*', '').strip()
        sector_by_stock_list.append({'업종명': sector, '종목명': stock_name})

  return pd.DataFrame(sector_by_stock_list)


def update_stocks(market, market_id):
  print(f"update_stocks() 호출됨: market={market}, market_id={market_id}")

  sector_df = fetch_sector_data()
  stock_df = fetch_stock_data(sector_df)

  # stock_df에 '업종명' 컬럼이 있는지 확인
  print(f"stock_df.columns: {stock_df.columns}")
  print(f"stock_df.head: {stock_df}")

  if market == 'ETF' :
    market_data = fdr.StockListing('ETF/KR')
  else :
    market_data = fdr.StockListing(market)


  market_data = market_data.merge(stock_df, left_on='Name', right_on='종목명', how='left')

  # 컬럼명 변경 확인
  print(f"market_data.columns (before rename): {market_data.columns}")

  market_data.rename(columns={'업종명': 'Sector'}, inplace=True)

  # Sector 컬럼이 없을 경우 기본값 추가
  if 'Sector' not in market_data.columns:
    print("market_data에서 'Sector' 컬럼이 없음 → 기본값 설정")
    market_data['Sector'] = '기타'

  # 최종 데이터 확인
  print(f"market_data.columns (after rename): {market_data.columns}")
  print(market_data.head())

  # KOSDAQ이면 '*' 추가 (동명의 KOSPI 업종과 구분하기 위함)
  if market == 'KOSDAQ':
    market_data['Sector'] = market_data['Sector'].astype(str) + '*'

  # ETF는 별도 Sector 매핑 적용
  if market == 'ETF':
    market_data.rename(columns={'Symbol': 'Code', 'MarCap': 'Marcap'}, inplace=True)
    category_decode = {
      1: '국내 시장지수', 2: '국내 업종/테마', 3: '국내파생',
      4: '해외주식', 5: '원자재', 6: '채권', 7: '기타ETF'
    }
    market_data['Sector'] = market_data['Category'].map(category_decode)

  print(f"market_data(after mapping): {market_data.head()}")

  sector_id_mapping = get_existing_sectors()

  for sector in sector_df['업종명']:
    if sector not in sector_id_mapping:
      sec_id = insert_new_sector(
        sector,
        sector_df[sector_df['업종명'] == sector]['업종코드'].values[0],
        market_id
      )
      sector_id_mapping[sector] = sec_id

  # STOCKS 테이블 업데이트
  for _, row in market_data.iterrows():
    sec_id = sector_id_mapping.get(row['Sector'])
    if sec_id:
      update_or_insert_stock(row['Code'], row['Name'], sec_id, market_id)
