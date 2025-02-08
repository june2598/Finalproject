-- ��õ ���� ����Ʈ �ҷ����� : ���ͷ� ��������
-- 
SELECT DISTINCT
    S.SEC_ID,
    C.SEC_NM,
    S.TRAIT_SEC_RTN,
    S.TRAIT_SEC_VOL,
    S.IS_REC,
    S.SEC_RISK
FROM
    MKT_SEC_STK C                       -- �� ����
JOIN
    TRAIT_SEC S ON C.SEC_ID = S.SEC_ID  -- ���� SEC_ID�� TRAIT_SEC�� SEC_ID�� ��ġ�ϴ� �͸� 
WHERE
    C.MARKET_ID = 1                     -- �ڽ��� ���常 ������
    AND S.SEC_RISK <= 2  
GROUP BY
    S.SEC_ID,
    C.SEC_NM,
    S.TRAIT_SEC_RTN,
    S.TRAIT_SEC_VOL,
    S.IS_REC,
    S.SEC_RISK
ORDER BY TRAIT_SEC_RTN DESC;           -- ���ͷ� ��������


-- ���� ��õ ǥ�� : ���� ���ͷ��� ���� ���� 3���� ��õǥ��
UPDATE TRAIT_SEC
SET IS_REC = 1
WHERE SEC_ID IN (
    SELECT SEC_ID
    FROM TRAIT_SEC
    ORDER BY TRAIT_SEC_RTN DESC
    FETCH FIRST 3 ROWS ONLY
);

-- ���� ��õ ǥ�� : ���� ���ͷ��� ���� ���� 3���� ��õǥ�� �ڽ��� ����

UPDATE TRAIT_SEC
SET IS_REC = 1
WHERE SEC_ID IN (
    SELECT SEC_ID
    FROM (
        SELECT DISTINCT             -- �信���� �� ������ �������� �ߺ��ؼ� �ֱ⶧����(�� ������ ������ ������ �ֱ⶧��), �ߺ��� ��������ߵ˴ϴ�.
            S.SEC_ID
        FROM 
            TRAIT_SEC S
        JOIN 
            MKT_SEC_STK C ON S.SEC_ID = C.SEC_ID
        WHERE 
            C.MARKET_ID = 1  -- KOSPI ���� ����
            AND S.SEC_RISK <= 2 
        ORDER BY 
            S.TRAIT_SEC_RTN DESC
        FETCH FIRST 3 ROWS ONLY  -- ���� ���� ���ͷ� ���� 3��
    )
);


-- ��õ�� �� ������ ���� IS_REC �ʱ�ȭ
UPDATE TRAIT_SEC
SET IS_REC = 0;


COMMIT;
ROLLBACK;



