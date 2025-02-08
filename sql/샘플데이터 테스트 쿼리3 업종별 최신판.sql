-- �������� ���� �� Ŀ�´�Ƽ �� �� �������� ����ϰ�, ���� �� �������� �������� �����ϴ� ����
SELECT 
    sec_nm,  -- ���� �̸�
    -- ���� �� ������
    ROUND(
        CASE 
            WHEN total_yesterday_news_count = 0 THEN NULL  -- ���� ���� ���� 0�� ��� �������� NULL�� ����
            ELSE (total_today_news_count - total_yesterday_news_count) / total_yesterday_news_count * 100 
        END, 2) AS NEWS_INCREASE_RATE,
    -- Ŀ�´�Ƽ �� �� ������
    ROUND(
        CASE 
            WHEN total_yesterday_community_count = 0 THEN NULL  -- ���� Ŀ�´�Ƽ �� ���� 0�� ��� �������� NULL�� ����
            ELSE (total_today_community_count - total_yesterday_community_count) / total_yesterday_community_count * 100 
        END, 2) AS COMMUNITY_INCREASE_RATE
FROM 
    (
        -- �� ���ͺ��� ������ ������ ���� �� Ŀ�´�Ƽ �� ���� ���
        SELECT 
            SEC_NM,  -- ���� �̸�
            -- ���� ���� �� �հ�
            SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_news_count,
            -- ���� ���� �� �հ�
            SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_news_count,
            -- ���� Ŀ�´�Ƽ �� �� �հ�
            SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_community_count,
            -- ���� Ŀ�´�Ƽ �� �� �հ�
            SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_community_count
        FROM 
            (
                -- ���� �����͸� �������� ���� ��������
                SELECT 
                    C.SEC_NM,  -- ���� �̸�
                    N.PUBLISHED_DATE,  -- ���� �Խ� ��¥
                    'NEWS' AS SOURCE  -- ������ ��ó�� 'NEWS'�� ����
                FROM 
                    MKT_SEC_STK C
                JOIN 
                    NEWS N ON C.STK_ID = N.STK_ID  -- ���� ���̺�� ����
                UNION ALL
                -- Ŀ�´�Ƽ �����͸� �������� ���� ��������
                SELECT 
                    C.SEC_NM,  -- ���� �̸�
                    CM.POST_DATE AS PUBLISHED_DATE,  -- Ŀ�´�Ƽ �Խ� ��¥
                    'COMMUNITY' AS SOURCE  -- ������ ��ó�� 'COMMUNITY'�� ����
                FROM 
                    MKT_SEC_STK C
                JOIN 
                    COMMUNITY CM ON C.STK_ID = CM.STK_ID  -- Ŀ�´�Ƽ ���̺�� ����
            ) T
        GROUP BY 
            SEC_NM  -- ���ͺ��� �׷�ȭ
    ) sector_counts
ORDER BY 
    NEWS_INCREASE_RATE DESC;  -- ���� �� �������� �������� �������� ����