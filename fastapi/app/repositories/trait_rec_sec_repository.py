import cx_Oracle

dsn = cx_Oracle.makedsn("localhost", 1521, service_name="xe")


def get_db_connection():
  """ 오라클 DB 연결 """
  return cx_Oracle.connect(user="c##PROJECT", password="k5002", dsn=dsn)


def create_or_replace_trait_rec_sec_view():
  """ TRAIT_REC_SEC_VIEW 뷰 생성 또는 업데이트 """
  sql = """
            CREATE OR REPLACE VIEW TRAIT_REC_SEC_VIEW AS
        SELECT
            ts.SEC_ID,
            CASE
                WHEN ts.TRAIT_SEC_RISK > 0 AND ts.TRAIT_SEC_RISK <= 1 THEN 1
                WHEN ts.TRAIT_SEC_RISK > 1 AND ts.TRAIT_SEC_RISK <= 2 THEN 2
                WHEN ts.TRAIT_SEC_RISK > 2 THEN 3
                ELSE NULL
            END AS TRAIT_SEC_RISK,
            ts.TRAIT_SEC_RTN,
            m.MARKET_ID,
            CASE
                WHEN m.MARKET_ID = 1 THEN
                    CASE
        
                        WHEN ts.TRAIT_SEC_RISK <= 3 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 3 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 3
        
                        WHEN ts.TRAIT_SEC_RISK <= 2 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 2 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 2
        
                        WHEN ts.TRAIT_SEC_RISK <= 1 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 1 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 1
                        ELSE 0
                    END
        
                WHEN m.MARKET_ID = 2 THEN
                    CASE
        
                        WHEN ts.TRAIT_SEC_RISK <= 3 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 3 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 3
        
                        WHEN ts.TRAIT_SEC_RISK <= 2 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 2 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 2
        
                        WHEN ts.TRAIT_SEC_RISK <= 1 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 1 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 1
                        ELSE 0
                    END
        
                WHEN m.MARKET_ID = 3 THEN
                    CASE
               
                        WHEN ts.TRAIT_SEC_RISK <= 3 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 3 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 3
            
                        WHEN ts.TRAIT_SEC_RISK <= 2 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 2 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 2
                  
                        WHEN ts.TRAIT_SEC_RISK <= 1 AND RANK() OVER (PARTITION BY m.MARKET_ID, CASE WHEN ts.TRAIT_SEC_RISK <= 1 THEN 1 END ORDER BY ts.TRAIT_SEC_RTN DESC) <= 3 THEN 1
                        ELSE 0
                    END
                ELSE 0
            END AS IS_REC
        FROM 
            TRAIT_SEC ts
        JOIN 
            MKT_SEC_STK m ON ts.SEC_ID = m.SEC_ID
        WHERE 
            TRUNC(ts.CDATE) = TRUNC(SYSDATE)    
        GROUP BY
            ts.SEC_ID, ts.TRAIT_SEC_RISK, ts.TRAIT_SEC_RTN, m.MARKET_ID
            """

  try:
    with get_db_connection() as conn:
      with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    return True, "TRAIT_REC_SEC_VIEW 생성 또는 업데이트 완료"
  except Exception as e:
    return False, f"뷰 생성 오류 발생: {e}"


def update_trait_rec_sec():
  """ TRAIT_REC_SEC 테이블 업데이트 실행 """
  sql = """
    INSERT INTO TRAIT_REC_SEC (TRAIT_REC_SEC_ID, SEC_ID, TRAIT_REC_SEC_RTN, TRAIT_REC_SEC_RISK, IS_REC, MARKET_ID, CDATE)
    SELECT
        TRAIT_REC_SEC_SEQ.NEXTVAL,  
        vr.SEC_ID,
        vr.TRAIT_SEC_RTN,
        vr.TRAIT_SEC_RISK,
        vr.IS_REC,
        vr.MARKET_ID,
        SYSDATE
    FROM TRAIT_REC_SEC_VIEW vr
    WHERE NOT EXISTS (
        SELECT 1
        FROM TRAIT_REC_SEC tr
        WHERE tr.SEC_ID = vr.SEC_ID 
          AND NVL(tr.SEC_ID, -1) IS NOT NULL
          AND TRUNC(tr.CDATE) = TRUNC(SYSDATE)
    )
    """

  try:
    with get_db_connection() as conn:
      with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    return True, "TRAIT_REC_SEC 업데이트 완료"
  except Exception as e:
    return False, f"오류 발생: {e}"
