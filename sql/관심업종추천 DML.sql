-- 추천 업종 리스트 불러오기 : 수익률 내림차순
-- 
SELECT DISTINCT
    S.SEC_ID,
    C.SEC_NM,
    S.TRAIT_SEC_RTN,
    S.TRAIT_SEC_VOL,
    S.IS_REC,
    S.SEC_RISK
FROM
    MKT_SEC_STK C                       -- 뷰 참조
JOIN
    TRAIT_SEC S ON C.SEC_ID = S.SEC_ID  -- 뷰의 SEC_ID와 TRAIT_SEC의 SEC_ID가 일치하는 것만 
WHERE
    C.MARKET_ID = 1                     -- 코스피 시장만 가져옴
    AND S.SEC_RISK <= 2  
GROUP BY
    S.SEC_ID,
    C.SEC_NM,
    S.TRAIT_SEC_RTN,
    S.TRAIT_SEC_VOL,
    S.IS_REC,
    S.SEC_RISK
ORDER BY TRAIT_SEC_RTN DESC;           -- 수익률 내림차순


-- 업종 추천 표시 : 가장 수익률이 높은 업종 3개에 추천표시
UPDATE TRAIT_SEC
SET IS_REC = 1
WHERE SEC_ID IN (
    SELECT SEC_ID
    FROM TRAIT_SEC
    ORDER BY TRAIT_SEC_RTN DESC
    FETCH FIRST 3 ROWS ONLY
);

-- 업종 추천 표시 : 가장 수익률이 높은 업종 3개에 추천표시 코스피 시장

UPDATE TRAIT_SEC
SET IS_REC = 1
WHERE SEC_ID IN (
    SELECT SEC_ID
    FROM (
        SELECT DISTINCT             -- 뷰에서는 한 업종에 여러행이 중복해서 있기때문에(한 업종내 종목이 여러개 있기때문), 중복을 제거해줘야됩니다.
            S.SEC_ID
        FROM 
            TRAIT_SEC S
        JOIN 
            MKT_SEC_STK C ON S.SEC_ID = C.SEC_ID
        WHERE 
            C.MARKET_ID = 1  -- KOSPI 시장 조건
            AND S.SEC_RISK <= 2 
        ORDER BY 
            S.TRAIT_SEC_RTN DESC
        FETCH FIRST 3 ROWS ONLY  -- 가장 높은 수익률 상위 3개
    )
);


-- 추천이 다 끝나고 나면 IS_REC 초기화
UPDATE TRAIT_SEC
SET IS_REC = 0;


COMMIT;
ROLLBACK;



