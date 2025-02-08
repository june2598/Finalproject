-- 업종별로 뉴스 및 커뮤니티 글 수 증가율을 계산하고, 뉴스 수 증가율을 기준으로 상위 5개 종목을 조회하는 쿼리
-- 각 종목별로 어제와 오늘의 뉴스 및 커뮤니티 글 수를 비교하여 증가율을 계산합니다.

SELECT 
    stk_nm,  -- 종목 이름
    sec_nm,  -- 섹터 이름
    -- 뉴스 수 증가율
    ROUND(
        CASE 
            -- 어제 뉴스 수가 0인 경우 증가율을 NULL로 설정
            WHEN (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL
            ELSE (
                -- 오늘 뉴스 수에서 어제 뉴스 수를 뺀 값을 어제 뉴스 수로 나누고 100을 곱해 증가율 계산
                (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - 
                (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID)
            ) / (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 
        END, 2) AS NEWS_INCREASE_RATE,
    -- 커뮤니티 글 수 증가율
    ROUND(
        CASE 
            -- 어제 커뮤니티 글 수가 0인 경우 증가율을 NULL로 설정
            WHEN (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL
            ELSE (
                -- 오늘 커뮤니티 글 수에서 어제 커뮤니티 글 수를 뺀 값을 어제 커뮤니티 글 수로 나누고 100을 곱해 증가율 계산
                (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - 
                (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID)
            ) / (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 
        END, 2) AS COMMUNITY_INCREASE_RATE
FROM 
    (
        -- 뉴스가 있는 종목과 해당 섹터 이름을 가져오는 서브쿼리
        SELECT stk_id, stk_nm, sec_nm
        FROM mkt_sec_stk
        WHERE STK_ID IN (SELECT DISTINCT STK_ID FROM NEWS)
    ) C
GROUP BY
    STK_ID, stk_nm, sec_nm  -- 종목 ID, 종목 이름, 섹터 이름으로 그룹화
ORDER BY 
    NEWS_INCREASE_RATE DESC  -- 뉴스 수 증가율을 기준으로 내림차순 정렬
FETCH FIRST 5 ROWS ONLY;  -- 상위 5개 종목만 조회