package com.kh.finalproject.domain.stockrecommendation.dao;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor

public class StockRecommendationDAOImpl implements StockRecommendationDAO {

  private final NamedParameterJdbcTemplate template;

  // 세션에서 성향 불러오는 메서드
  private MemberTraits getMemberTraits(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return (MemberTraits) session.getAttribute("memberTraits");
  }

  @Override
  public List<RecStk> listByTraitSector(HttpServletRequest request, String inputDate) {

    // 성향 정보 불러오기
    MemberTraits memberTraits = getMemberTraits(request);

    StringBuffer sql = new StringBuffer();

    sql.append(" WITH STOCK_PRICES AS ( ");
    sql.append("     SELECT ");
    sql.append("     STK_ID, ");
    sql.append("     MAX(CASE WHEN TRADE_DATE = (SELECT MAX(TRADE_DATE) ");
    sql.append("         FROM REC_STK ");
    sql.append("         WHERE TRADE_DATE <= TRUNC(SYSDATE)) ");
    sql.append("     THEN REC_PRICE END) AS TODAY_PRICE, ");
    sql.append("  ");
    sql.append(" MAX(CASE WHEN TRADE_DATE = (SELECT MIN(TRADE_DATE) ");
    sql.append("     FROM REC_STK ");
    sql.append("     WHERE TRADE_DATE >= TO_DATE(:inputDate, 'YYYY-MM-DD')) ");
    sql.append(" THEN REC_PRICE END) AS START_PRICE ");
    sql.append(" FROM REC_STK ");
    sql.append(" WHERE TRADE_DATE BETWEEN TO_DATE(:inputDate, 'YYYY-MM-DD') AND TRUNC(SYSDATE) ");
    sql.append("     GROUP BY STK_ID ");
    sql.append(" ), ");
    sql.append(" DAILY_RETURNS AS ( ");
    sql.append("     SELECT ");
    sql.append(" STK_ID, ");
    sql.append("     TRADE_DATE, ");
    sql.append("     ((REC_PRICE - LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE)) ");
    sql.append("      / LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE)) * 100 AS DAILY_RETURN ");
    sql.append(" FROM REC_STK ");
    sql.append(" WHERE TRADE_DATE BETWEEN TO_DATE(:inputDate, 'YYYY-MM-DD') AND TRUNC(SYSDATE) ");
    sql.append(" ), ");
    sql.append(" STOCK_RET AS ( ");
    sql.append("     SELECT ");
    sql.append(" P.STK_ID, ");
    sql.append("     ROUND(((P.TODAY_PRICE - P.START_PRICE) / P.START_PRICE) * 100, 2) AS recRtn, ");
    sql.append(" ROUND(STDDEV(D.DAILY_RETURN), 2) AS recVol ");
    sql.append(" FROM STOCK_PRICES P ");
    sql.append(" JOIN DAILY_RETURNS D ON P.STK_ID = D.STK_ID ");
    sql.append(" GROUP BY P.STK_ID, P.TODAY_PRICE, P.START_PRICE ");
    sql.append(" ), ");
    sql.append(" VOL_QUARTILES AS ( ");
    sql.append("     SELECT ");
    sql.append(" MARKET_ID, ");
    sql.append("     ROUND(PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY recVol), 2) AS Q1, ");
    sql.append(" ROUND(PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY recVol), 2) AS Q3 ");
    sql.append(" FROM STOCK_RET R ");
    sql.append(" JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID ");
    sql.append(" GROUP BY MARKET_ID ");
    sql.append(" ), ");
    sql.append(" STOCK_RISK AS ( ");
    sql.append("     SELECT ");
    sql.append(" R.STK_ID AS stkId, ");
    sql.append("     S.MARKET_ID AS marketId, ");
    sql.append("     S.SEC_ID AS secId, ");
    sql.append("     S.SEC_NM AS secNm, ");
    sql.append("     S.STK_NM AS stkNm, ");
    sql.append("     R.recRtn, ");
    sql.append("     R.recVol, ");
    sql.append("     CASE ");
    sql.append(" WHEN R.recVol <= V.Q1 THEN 1 ");
    sql.append(" WHEN R.recVol <= V.Q3 THEN 2 ");
    sql.append(" ELSE 3 ");
    sql.append(" END AS recRisk ");
    sql.append(" FROM STOCK_RET R ");
    sql.append(" JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID ");
    sql.append(" JOIN VOL_QUARTILES V ON S.MARKET_ID = V.MARKET_ID ");
    sql.append(" ) ");
    sql.append("     SELECT ");
    sql.append(" stkId, marketId, secId, secNm, stkNm, recRtn, recVol, recRisk ");
    sql.append(" FROM STOCK_RISK ");
    sql.append(" WHERE secId IN (:intSec)   ");
    sql.append(" AND recRisk <= :memberRisk           ");
    sql.append(" AND recRtn  >= :expRtn ");
    sql.append(" ORDER BY recRtn DESC NULLS LAST ");
    sql.append(" FETCH FIRST 10 ROWS ONLY ");

    int memberRisk = memberTraits.getMemberRisk();
    double expRtn = memberTraits.getExpRtn();

    // intSec를 문자열로 받고, 이를 Integer 리스트로 변환
    String intSecString = String.join(",", memberTraits.getIntSec()); // 예: "31,59"
    List<Integer> intSec = Arrays.stream(intSecString.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("inputDate",inputDate)
        .addValue("memberRisk",memberRisk)
        .addValue("expRtn",expRtn)
        .addValue("intSec",intSec);

    log.info("Member Risk: {}", memberRisk);
    log.info("Expected Return: {}", expRtn);
    log.info("Interest Sector IDs: {}", intSec);


    List<RecStk> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RecStk.class));
    return list;
  }

  // 관심업종없을떄 추천

  @Override
  public List<RecStk> listWithoutTraitSector(HttpServletRequest request, String inputDate) {

    // 성향 정보 불러오기
    MemberTraits memberTraits = getMemberTraits(request);

    StringBuffer sql = new StringBuffer();
    sql.append(" WITH STOCK_PRICES AS ( ");
    sql.append("     SELECT ");
    sql.append("     STK_ID, ");
    sql.append("     MAX(CASE WHEN TRADE_DATE = (SELECT MAX(TRADE_DATE) ");
    sql.append("         FROM REC_STK ");
    sql.append("         WHERE TRADE_DATE <= TRUNC(SYSDATE)) ");
    sql.append("     THEN REC_PRICE END) AS TODAY_PRICE, ");
    sql.append("  ");
    sql.append(" MAX(CASE WHEN TRADE_DATE = (SELECT MIN(TRADE_DATE) ");
    sql.append("     FROM REC_STK ");
    sql.append("     WHERE TRADE_DATE >= TO_DATE(:inputDate, 'YYYY-MM-DD')) ");
    sql.append(" THEN REC_PRICE END) AS START_PRICE ");
    sql.append(" FROM REC_STK ");
    sql.append(" WHERE TRADE_DATE BETWEEN TO_DATE(:inputDate, 'YYYY-MM-DD') AND TRUNC(SYSDATE) ");
    sql.append("     GROUP BY STK_ID ");
    sql.append(" ), ");
    sql.append(" DAILY_RETURNS AS ( ");
    sql.append("     SELECT ");
    sql.append(" STK_ID, ");
    sql.append("     TRADE_DATE, ");
    sql.append("     ((REC_PRICE - LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE)) ");
    sql.append("      / LAG(REC_PRICE) OVER (PARTITION BY STK_ID ORDER BY TRADE_DATE)) * 100 AS DAILY_RETURN ");
    sql.append(" FROM REC_STK ");
    sql.append(" WHERE TRADE_DATE BETWEEN TO_DATE(:inputDate, 'YYYY-MM-DD') AND TRUNC(SYSDATE) ");
    sql.append(" ), ");
    sql.append(" STOCK_RET AS ( ");
    sql.append("     SELECT ");
    sql.append(" P.STK_ID, ");
    sql.append("     ROUND(((P.TODAY_PRICE - P.START_PRICE) / P.START_PRICE) * 100, 2) AS recRtn, ");
    sql.append(" ROUND(STDDEV(D.DAILY_RETURN), 2) AS recVol ");
    sql.append(" FROM STOCK_PRICES P ");
    sql.append(" JOIN DAILY_RETURNS D ON P.STK_ID = D.STK_ID ");
    sql.append(" GROUP BY P.STK_ID, P.TODAY_PRICE, P.START_PRICE ");
    sql.append(" ), ");
    sql.append(" VOL_QUARTILES AS ( ");
    sql.append("     SELECT ");
    sql.append(" MARKET_ID, ");
    sql.append("     ROUND(PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY recVol), 2) AS Q1, ");
    sql.append(" ROUND(PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY recVol), 2) AS Q3 ");
    sql.append(" FROM STOCK_RET R ");
    sql.append(" JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID ");
    sql.append(" GROUP BY MARKET_ID ");
    sql.append(" ), ");
    sql.append(" STOCK_RISK AS ( ");
    sql.append("     SELECT ");
    sql.append(" R.STK_ID AS stkId, ");
    sql.append("     S.MARKET_ID AS marketId, ");
    sql.append("     S.SEC_ID AS secId, ");
    sql.append("     S.SEC_NM AS secNm, ");
    sql.append("     S.STK_NM AS stkNm, ");
    sql.append("     R.recRtn, ");
    sql.append("     R.recVol, ");
    sql.append("     CASE ");
    sql.append(" WHEN R.recVol <= V.Q1 THEN 1 ");
    sql.append(" WHEN R.recVol <= V.Q3 THEN 2 ");
    sql.append(" ELSE 3 ");
    sql.append(" END AS recRisk ");
    sql.append(" FROM STOCK_RET R ");
    sql.append(" JOIN MKT_SEC_STK S ON R.STK_ID = S.STK_ID ");
    sql.append(" JOIN VOL_QUARTILES V ON S.MARKET_ID = V.MARKET_ID ");
    sql.append(" ) ");
    sql.append("     SELECT ");
    sql.append(" stkId, marketId, secId, secNm, stkNm, recRtn, recVol, recRisk ");
    sql.append(" FROM STOCK_RISK ");
    sql.append(" WHERE recRisk <= :memberRisk           ");
    sql.append(" AND recRtn  >= :expRtn ");
    sql.append(" ORDER BY recRtn DESC NULLS LAST ");
    sql.append(" FETCH FIRST 20 ROWS ONLY ");

    int memberRisk = memberTraits.getMemberRisk();
    double expRtn = memberTraits.getExpRtn();

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("inputDate",inputDate)
        .addValue("memberRisk",memberRisk)
        .addValue("expRtn",expRtn);

    log.info("Member Risk: {}", memberRisk);
    log.info("Expected Return: {}", expRtn);

    List<RecStk> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RecStk.class));
    return list;

  }

  // 업종 ID로 업종명 찾기

  @Override
  public String findIntSecNmByIntSecId(HttpServletRequest request) {

    // 성향 정보 불러오기
    MemberTraits memberTraits = getMemberTraits(request);

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT DISTINCT SEC_NM ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" JOIN MEMBER_TRAITS t ON REGEXP_LIKE(t.INT_SEC, '(^|,)' || m.SEC_ID || '($|,)') ");
    sql.append(" WHERE t.MEMBER_SEQ = :memberSeq ");

    Long memberSeq = memberTraits.getMemberSeq();

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberSeq", memberSeq);

    // 업종명을 리스트로 받음
    List<String> secNm = template.query(sql.toString(), param, (rs, rowNum) -> rs.getString("SEC_NM"));

    // 리스트의 요소를 콤마로 구분된 문자열로 결합
    return String.join(", ", secNm);
  }

  // DTO에서 업종 ID로 업종명 찾기
  @Override
  public String findIntSecNmByIntSecIdFromDto(MemberTraitsDto memberTraitsDto) {

    // DTO에서 관심 업종 ID 리스트 가져오기
    List<String> intSecList = memberTraitsDto.getIntSec();
    if (intSecList == null || intSecList.isEmpty()) {
      return "";
    }

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT DISTINCT SEC_NM ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" WHERE SEC_ID IN (:intSecList) ");

    Long memberSeq = memberTraitsDto.getMemberSeq();

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("intSecList", intSecList);

    // 업종명을 리스트로 받음
    List<String> secNm = template.query(sql.toString(), param, (rs, rowNum) -> rs.getString("SEC_NM"));


    // 리스트의 요소를 콤마로 구분된 문자열로 결합
    return String.join(", ", secNm);
  }
}
