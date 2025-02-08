-- �������� ���� �� Ŀ�´�Ƽ �� �� �������� ����ϰ�, ���� �� �������� �������� ���� 5�� ������ ��ȸ�ϴ� ����
-- �� ���񺰷� ������ ������ ���� �� Ŀ�´�Ƽ �� ���� ���Ͽ� �������� ����մϴ�.

SELECT 
    stk_nm,  -- ���� �̸�
    sec_nm,  -- ���� �̸�
    -- ���� �� ������
    ROUND(
        CASE 
            -- ���� ���� ���� 0�� ��� �������� NULL�� ����
            WHEN (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL
            ELSE (
                -- ���� ���� ������ ���� ���� ���� �� ���� ���� ���� ���� ������ 100�� ���� ������ ���
                (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - 
                (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID)
            ) / (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 
        END, 2) AS NEWS_INCREASE_RATE,
    -- Ŀ�´�Ƽ �� �� ������
    ROUND(
        CASE 
            -- ���� Ŀ�´�Ƽ �� ���� 0�� ��� �������� NULL�� ����
            WHEN (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL
            ELSE (
                -- ���� Ŀ�´�Ƽ �� ������ ���� Ŀ�´�Ƽ �� ���� �� ���� ���� Ŀ�´�Ƽ �� ���� ������ 100�� ���� ������ ���
                (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - 
                (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID)
            ) / (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 
        END, 2) AS COMMUNITY_INCREASE_RATE
FROM 
    (
        -- ������ �ִ� ����� �ش� ���� �̸��� �������� ��������
        SELECT stk_id, stk_nm, sec_nm
        FROM mkt_sec_stk
        WHERE STK_ID IN (SELECT DISTINCT STK_ID FROM NEWS)
    ) C
GROUP BY
    STK_ID, stk_nm, sec_nm  -- ���� ID, ���� �̸�, ���� �̸����� �׷�ȭ
ORDER BY 
    NEWS_INCREASE_RATE DESC  -- ���� �� �������� �������� �������� ����
FETCH FIRST 5 ROWS ONLY;  -- ���� 5�� ���� ��ȸ