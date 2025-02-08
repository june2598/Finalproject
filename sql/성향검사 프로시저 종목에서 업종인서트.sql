DECLARE
    v_trait_sec_id NUMBER;
BEGIN
    -- ������ ��� ��� �� TRAIT_SEC�� ����
    FOR rec IN (
        SELECT 
            C.SEC_ID,                -- ���� ID
            AVG(N.TRAIT_STK_RTN) AS TRAIT_SEC_RTN,  -- ������ ���ͷ� ���
            AVG(N.TRAIT_STK_VOL) AS TRAIT_SEC_VOL,  -- ������ ������ ���
            AVG(N.TRAIT_STK_RISK) AS SEC_RISK       -- ������ ���赵 ���
        FROM 
            mkt_sec_stk C
        JOIN 
            TRAIT_STK N ON C.STK_ID = N.STK_ID
        GROUP BY 
            C.SEC_ID
    ) LOOP
        -- ������ ���� ������ ����
        SELECT trait_sec_seq.NEXTVAL INTO v_trait_sec_id FROM dual;
        
        -- ���� ���� ����Ͽ� INSERT ����
        INSERT INTO TRAIT_SEC (TRAIT_SEC_ID, SEC_ID, TRAIT_SEC_RTN, TRAIT_SEC_VOL, SEC_RISK)
        VALUES (v_trait_sec_id, rec.SEC_ID, rec.TRAIT_SEC_RTN, rec.TRAIT_SEC_VOL, rec.SEC_RISK);
    END LOOP;
    -- ���� ���� ����
    COMMIT; 
END;

