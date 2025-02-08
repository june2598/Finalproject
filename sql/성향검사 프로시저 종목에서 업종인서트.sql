DECLARE
    v_trait_sec_id NUMBER;
BEGIN
    -- 업종별 통계 계산 및 TRAIT_SEC에 삽입
    FOR rec IN (
        SELECT 
            C.SEC_ID,                -- 업종 ID
            AVG(N.TRAIT_STK_RTN) AS TRAIT_SEC_RTN,  -- 업종별 수익률 평균
            AVG(N.TRAIT_STK_VOL) AS TRAIT_SEC_VOL,  -- 업종별 변동성 평균
            AVG(N.TRAIT_STK_RISK) AS SEC_RISK       -- 업종별 위험도 평균
        FROM 
            mkt_sec_stk C
        JOIN 
            TRAIT_STK N ON C.STK_ID = N.STK_ID
        GROUP BY 
            C.SEC_ID
    ) LOOP
        -- 시퀀스 값을 변수에 저장
        SELECT trait_sec_seq.NEXTVAL INTO v_trait_sec_id FROM dual;
        
        -- 변수 값을 사용하여 INSERT 실행
        INSERT INTO TRAIT_SEC (TRAIT_SEC_ID, SEC_ID, TRAIT_SEC_RTN, TRAIT_SEC_VOL, SEC_RISK)
        VALUES (v_trait_sec_id, rec.SEC_ID, rec.TRAIT_SEC_RTN, rec.TRAIT_SEC_VOL, rec.SEC_RISK);
    END LOOP;
    -- 변경 사항 저장
    COMMIT; 
END;

