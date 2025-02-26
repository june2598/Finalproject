import cx_Oracle

dsn = cx_Oracle.makedsn("localhost", 1521, service_name="xe")

def get_db_connection():
    """ 오라클 DB 연결 """
    return cx_Oracle.connect(user="c##PROJECT", password="k5002", dsn=dsn)

def update_trait_sec():
    """ TRAIT_SEC 테이블 업데이트 실행 """
    sql = """
    DECLARE
        v_trait_sec_id NUMBER;
        v_today DATE := TRUNC(SYSDATE);
    BEGIN
        FOR rec IN (
            SELECT 
                C.SEC_ID, 
                AVG(N.TRAIT_STK_RTN) AS TRAIT_SEC_RTN, 
                AVG(N.TRAIT_STK_VOL) AS TRAIT_SEC_VOL, 
                AVG(N.TRAIT_STK_RISK) AS TRAIT_SEC_RISK
            FROM mkt_sec_stk C
            JOIN TRAIT_STK N ON C.STK_ID = N.STK_ID
            WHERE TRUNC(N.CDATE) = v_today
            GROUP BY C.SEC_ID
        ) LOOP
            BEGIN
                SELECT COUNT(*) INTO v_trait_sec_id
                FROM TRAIT_SEC
                WHERE SEC_ID = rec.SEC_ID AND CDATE = v_today;

                IF v_trait_sec_id = 0 THEN
                    SELECT trait_sec_seq.NEXTVAL INTO v_trait_sec_id FROM dual;
                    INSERT INTO TRAIT_SEC (TRAIT_SEC_ID, SEC_ID, CDATE, TRAIT_SEC_RTN, TRAIT_SEC_VOL, TRAIT_SEC_RISK)
                    VALUES (v_trait_sec_id, rec.SEC_ID, SYSDATE, rec.TRAIT_SEC_RTN, rec.TRAIT_SEC_VOL, rec.TRAIT_SEC_RISK);
                END IF;
            EXCEPTION
                WHEN OTHERS THEN
                    ROLLBACK;
                    RAISE;
            END;
        END LOOP;
        COMMIT;
    END;
    """

    try:
        with get_db_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(sql)
                conn.commit()
        return True, "TRAIT_SEC 업데이트 완료"
    except Exception as e:
        return False, f"오류 발생: {e}"
