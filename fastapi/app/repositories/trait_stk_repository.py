import cx_Oracle

dsn = cx_Oracle.makedsn("localhost", 1521, service_name="xe")

def get_db_connection():
    """ 오라클 DB 연결 """
    return cx_Oracle.connect(user="c##PROJECT", password="k5002", dsn=dsn)

def insert_trait_stk():
    """ `STOCK_RISK` 데이터를 `TRAIT_STK` 테이블에 삽입 """
    insert_sql = """
    INSERT INTO TRAIT_STK (
        TRAIT_STK_ID, STK_ID, TRAIT_STK_RTN, TRAIT_STK_VOL, TRAIT_STK_RISK
    )
    SELECT 
        TRAIT_STK_SEQ.NEXTVAL, 
        stkId, 
        recRtn, 
        recVol, 
        recRisk
    FROM (    
        WITH STOCK_PRICES AS (
            SELECT 
                STK_ID,
                MAX(CASE 
                    WHEN TRADE_DATE = (SELECT MAX(TRADE_DATE) 
                                       FROM REC_STK 
                                       WHERE TRADE_DATE <= TRUNC(SYSDATE)) 
                    THEN REC_PRICE 
                END) AS TODAY_PRICE,
                MAX(CASE 
                    WHEN TRADE_DATE = (SELECT MIN(TRADE_DATE) 
                                       FROM REC_STK 
                                       WHERE TRADE_DATE >= TRUNC(ADD_MONTHS(SYSDATE, -1))) 
                    THEN REC_PRICE 
                END) AS START_PRICE
            FROM REC_STK
            WHERE TRADE_DATE BETWEEN TRUNC(ADD_MONTHS(SYSDATE, -1)) AND TRUNC(SYSDATE)
            GROUP BY STK_ID
        ),
        DAILY_RETURNS AS (
            SELECT 
                STK_ID,
                TRADE_DATE,
                ((REC_PRICE - LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE)) 
                 / NULLIF(LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE), 0)) * 100 AS DAILY_RETURN
            FROM REC_STK
            WHERE TRADE_DATE BETWEEN TRUNC(ADD_MONTHS(SYSDATE, -1)) AND TRUNC(SYSDATE)
        ),
        STOCK_RET AS (
            SELECT 
                P.STK_ID,
                ROUND(((NVL(P.TODAY_PRICE, 0) - NVL(P.START_PRICE, 0)) / NULLIF(P.START_PRICE, 0)) * 100, 2) AS recRtn,
                ROUND(STDDEV(NVL(D.DAILY_RETURN, 0)), 2) AS recVol
            FROM STOCK_PRICES P
            JOIN DAILY_RETURNS D ON P.STK_ID = D.STK_ID
            GROUP BY P.STK_ID, P.TODAY_PRICE, P.START_PRICE
        ),
        VOL_QUARTILES AS (
            SELECT 
                S.MARKET_ID,
                ROUND(PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY R.recVol), 2) AS Q1,
                ROUND(PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY R.recVol), 2) AS Q3
            FROM STOCK_RET R
            JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID
            GROUP BY S.MARKET_ID
        ),
        STOCK_RISK AS (
            SELECT 
                R.STK_ID AS stkId,
                R.recRtn,
                R.recVol,
                CASE 
                    WHEN R.recVol <= V.Q1 THEN 1
                    WHEN R.recVol <= V.Q3 THEN 2
                    ELSE 3
                END AS recRisk
            FROM STOCK_RET R
            JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID
            JOIN VOL_QUARTILES V ON S.MARKET_ID = V.MARKET_ID
        )
        SELECT stkId, recRtn, recVol, recRisk FROM STOCK_RISK
    ) 
    WHERE recRtn IS NOT NULL
    """

    try:
        with get_db_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(insert_sql)
                conn.commit()
        return True, "TRAIT_STK 업데이트 완료"
    except Exception as e:
        return False, f"오류 발생: {e}"
