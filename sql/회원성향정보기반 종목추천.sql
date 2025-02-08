SELECT DISTINCT
--    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    R.REC_RTN,
--    S.TRAIT_STK_VOL,
    R.REC_RISK
FROM
    MKT_SEC_STK C                       -- �� ����
JOIN
    REC_STK R ON C.STK_ID = R.STK_ID  -- ���� STK_ID�� TRAIT_STK�� STK_ID�� ��ġ�ϴ� �͸� 
WHERE
    -- MEMBER_TRAITS���� MEMBER_RISK �����ͼ� ���͸�
    R.REC_RISK <= (
        SELECT MEMBER_RISK 
        FROM MEMBER_TRAITS 
        WHERE MEMBER_ID = 'test1234' -- ȸ�� ID
    )
    -- ȸ���� ���� ���� ID �����ͼ� ���͸�
    AND C.SEC_ID IN (                   
        SELECT SEC_ID   
        FROM MKT_SEC_STK                 
        WHERE SEC_NM IN (
            SELECT TRIM(REGEXP_SUBSTR(INT_SEC, '[^, ]+', 1, LEVEL))     -- INT_SEC���� �� �������� ����. ,�� ������ �����ڷ� ���ɾ����� �Էµ��ִ°� �ϳ��� ������ ��
            FROM MEMBER_TRAITS 
            WHERE MEMBER_ID = 'test1234' -- ȸ�� ID
            CONNECT BY REGEXP_SUBSTR(INT_SEC, '[^, ]+', 1, LEVEL) IS NOT NULL -- �ݺ������� �������� ����
        )
    )     
    -- MEMBER_TRAITS���� EXP_RTN �����ͼ� ���͸�
    AND R.REC_RTN >= (
        SELECT EXP_RTN 
        FROM MEMBER_TRAITS 
        WHERE MEMBER_ID = 'test1234' -- ȸ�� ID
    )                                   
GROUP BY
--    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    R.REC_RTN,
--    S.TRAIT_STK_VOL,
    R.REC_RISK
ORDER BY 
    R.REC_RTN DESC;             -- ���ͷ� ��������
