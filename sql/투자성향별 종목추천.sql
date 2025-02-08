SELECT DISTINCT
    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    S.TRAIT_STK_RTN,
    S.TRAIT_STK_VOL,
    S.TRAIT_STK_RISK
FROM
    MKT_SEC_STK C                       -- �� ����
JOIN
    TRAIT_STK S ON C.STK_ID = S.STK_ID  -- ���� SEC_ID�� TRAIT_SEC�� SEC_ID�� ��ġ�ϴ� �͸� 
WHERE
    S.TRAIT_STK_RISK <= 2                   -- ���赵 2����  (�� ���� ���� ����)
    AND C.SEC_ID IN (31, 59)                -- ����, ���ձ�� ���� (C.SEC_ID =11, C.SEC_ID =4)
    AND S.TRAIT_STK_RTN >= 7.1              -- ���ͷ� 7.1% �̻� (�� ���� ���� ����)
     
GROUP BY
    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    S.TRAIT_STK_RTN,
    S.TRAIT_STK_VOL,
    S.TRAIT_STK_RISK
ORDER BY S.TRAIT_STK_RTN DESC;           -- ���ͷ� ��������


