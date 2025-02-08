SELECT 
    sec_nm,
    -- 뉴스 수 증가율
    ROUND(
        CASE 
            WHEN total_yesterday_news_count = 0 THEN NULL
            ELSE (total_today_news_count - total_yesterday_news_count) / total_yesterday_news_count * 100 
        END, 2) AS NEWS_INCREASE_RATE
FROM 
    (
        SELECT 
            SEC_NM,
            SUM(CASE WHEN TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_news_count,
            SUM(CASE WHEN TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_news_count
        FROM 
            MKT_SEC_STK C
        JOIN 
            NEWS N ON C.STK_ID = N.STK_ID
        GROUP BY 
            SEC_NM
    ) sector_news_counts
ORDER BY 
    NEWS_INCREASE_RATE DESC;