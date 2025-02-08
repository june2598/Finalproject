SELECT DISTINCT
    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    S.TRAIT_STK_RTN,
    S.TRAIT_STK_VOL,
    S.TRAIT_STK_RISK
FROM
    MKT_SEC_STK C                       -- 뷰 참조
JOIN
    TRAIT_STK S ON C.STK_ID = S.STK_ID  -- 뷰의 SEC_ID와 TRAIT_SEC의 SEC_ID가 일치하는 것만 
WHERE
    S.TRAIT_STK_RISK <= 2                   -- 위험도 2이하  (고객 성향 동적 정보)
    AND C.SEC_ID IN (31, 59)                -- 조선, 복합기업 업종 (C.SEC_ID =11, C.SEC_ID =4)
    AND S.TRAIT_STK_RTN >= 7.1              -- 수익률 7.1% 이상 (고객 성향 동적 정보)
     
GROUP BY
    S.STK_ID,
    C.SEC_NM,
    C.STK_NM,
    S.TRAIT_STK_RTN,
    S.TRAIT_STK_VOL,
    S.TRAIT_STK_RISK
ORDER BY S.TRAIT_STK_RTN DESC;           -- 수익률 내림차순


