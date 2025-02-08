SELECT DISTINCT
--    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    R.REC_RTN,
--    S.TRAIT_STK_VOL,
    R.REC_RISK
FROM
    MKT_SEC_STK C                       -- 뷰 참조
JOIN
    REC_STK R ON C.STK_ID = R.STK_ID  -- 뷰의 STK_ID와 TRAIT_STK의 STK_ID가 일치하는 것만 
WHERE
    -- MEMBER_TRAITS에서 MEMBER_RISK 가져와서 필터링
    R.REC_RISK <= (
        SELECT MEMBER_RISK 
        FROM MEMBER_TRAITS 
        WHERE MEMBER_ID = 'test1234' -- 회원 ID
    )
    -- 회원의 관심 업종 ID 가져와서 필터링
    AND C.SEC_ID IN (                   
        SELECT SEC_ID   
        FROM MKT_SEC_STK                 
        WHERE SEC_NM IN (
            SELECT TRIM(REGEXP_SUBSTR(INT_SEC, '[^, ]+', 1, LEVEL))     -- INT_SEC에서 각 업종명을 추출. ,와 공백을 구분자로 관심업종이 입력되있는걸 하나씩 꺼내서 비교
            FROM MEMBER_TRAITS 
            WHERE MEMBER_ID = 'test1234' -- 회원 ID
            CONNECT BY REGEXP_SUBSTR(INT_SEC, '[^, ]+', 1, LEVEL) IS NOT NULL -- 반복적으로 업종명을 추출
        )
    )     
    -- MEMBER_TRAITS에서 EXP_RTN 가져와서 필터링
    AND R.REC_RTN >= (
        SELECT EXP_RTN 
        FROM MEMBER_TRAITS 
        WHERE MEMBER_ID = 'test1234' -- 회원 ID
    )                                   
GROUP BY
--    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    R.REC_RTN,
--    S.TRAIT_STK_VOL,
    R.REC_RISK
ORDER BY 
    R.REC_RTN DESC;             -- 수익률 내림차순
