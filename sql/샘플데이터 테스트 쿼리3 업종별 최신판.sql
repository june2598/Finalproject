-- 업종별로 뉴스 및 커뮤니티 글 수 증가율을 계산하고, 뉴스 수 증가율을 기준으로 정렬하는 쿼리
SELECT 
    sec_nm,  -- 섹터 이름
    -- 뉴스 수 증가율
    ROUND(
        CASE 
            WHEN total_yesterday_news_count = 0 THEN NULL  -- 어제 뉴스 수가 0인 경우 증가율을 NULL로 설정
            ELSE (total_today_news_count - total_yesterday_news_count) / total_yesterday_news_count * 100 
        END, 2) AS NEWS_INCREASE_RATE,
    -- 커뮤니티 글 수 증가율
    ROUND(
        CASE 
            WHEN total_yesterday_community_count = 0 THEN NULL  -- 어제 커뮤니티 글 수가 0인 경우 증가율을 NULL로 설정
            ELSE (total_today_community_count - total_yesterday_community_count) / total_yesterday_community_count * 100 
        END, 2) AS COMMUNITY_INCREASE_RATE
FROM 
    (
        -- 각 섹터별로 어제와 오늘의 뉴스 및 커뮤니티 글 수를 계산
        SELECT 
            SEC_NM,  -- 섹터 이름
            -- 어제 뉴스 수 합계
            SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_news_count,
            -- 오늘 뉴스 수 합계
            SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_news_count,
            -- 어제 커뮤니티 글 수 합계
            SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_community_count,
            -- 오늘 커뮤니티 글 수 합계
            SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_community_count
        FROM 
            (
                -- 뉴스 데이터를 가져오기 위한 서브쿼리
                SELECT 
                    C.SEC_NM,  -- 섹터 이름
                    N.PUBLISHED_DATE,  -- 뉴스 게시 날짜
                    'NEWS' AS SOURCE  -- 데이터 출처를 'NEWS'로 지정
                FROM 
                    MKT_SEC_STK C
                JOIN 
                    NEWS N ON C.STK_ID = N.STK_ID  -- 뉴스 테이블과 조인
                UNION ALL
                -- 커뮤니티 데이터를 가져오기 위한 서브쿼리
                SELECT 
                    C.SEC_NM,  -- 섹터 이름
                    CM.POST_DATE AS PUBLISHED_DATE,  -- 커뮤니티 게시 날짜
                    'COMMUNITY' AS SOURCE  -- 데이터 출처를 'COMMUNITY'로 지정
                FROM 
                    MKT_SEC_STK C
                JOIN 
                    COMMUNITY CM ON C.STK_ID = CM.STK_ID  -- 커뮤니티 테이블과 조인
            ) T
        GROUP BY 
            SEC_NM  -- 섹터별로 그룹화
    ) sector_counts
ORDER BY 
    NEWS_INCREASE_RATE DESC;  -- 뉴스 수 증가율을 기준으로 내림차순 정렬