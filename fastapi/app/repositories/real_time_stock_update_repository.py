import cx_Oracle

dsn = cx_Oracle.makedsn('localhost', 1521, service_name='xe')

def get_db_connection() :
  return cx_Oracle.connect(user='c##PROJECT',password='k5002',dsn=dsn)


# STOCKS 테이블에서 STK_ID 조회
def get_stock_id_mapping(stock_codes):

  stock_id_mapping = {}
  missing_codes = []

  with get_db_connection() as conn:
    with conn.cursor() as cursor:
      for code in stock_codes:
        cursor.execute("SELECT STK_ID FROM STOCKS WHERE STK_CODE = :code", {'code': code})
        result = cursor.fetchone()
        if result:
          stock_id_mapping[code] = result[0]
        else:
          missing_codes.append(code)

      return stock_id_mapping, missing_codes

def insert_rt_stk_data(data):
  """RT_STK 테이블에 데이터 삽입"""
  with get_db_connection() as conn:
    with conn.cursor() as cursor:
      for _, row in data.iterrows():
        try:
          cursor.execute("""
                      INSERT INTO RT_STK (RT_STK_ID, STK_ID, PRICE, CHANGE, CHANGE_RATIO, AMOUNT, VOLUME, MARCAP)
                      VALUES (RT_STK_SEQ.NEXTVAL, :stk_id, :price, :change, :change_ratio, :amount, :volume, :marcap)
                  """, {
            'stk_id': row['STK_ID'],
            'price': row['PRICE'],
            'change': row['CHANGE'],
            'change_ratio': row['CHANGE_RATIO'],
            'amount': row['AMOUNT'],
            'volume': row['VOLUME'],
            'marcap': row['MARCAP']
          })
        except cx_Oracle.DatabaseError as e:
          error, = e.args
          print(f"DB Insert Error: {error.message}")

      conn.commit()



