package com.kh.finalproject.domain.stockrecommendation.dao;

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
  public List<RecStk> listByTraitSector(HttpServletRequest request) {

    // 성향 정보 불러오기
    MemberTraits memberTraits = getMemberTraits(request);

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT DISTINCT ");
    sql.append("S.REC_STK_ID, ");
    sql.append("    C.SEC_NM, ");
    sql.append("    C.STK_NM, ");
    sql.append("    S.REC_RTN, ");
    sql.append("    S.REC_VOL, ");
    sql.append("    S.REC_RISK ");
    sql.append("FROM ");
    sql.append("MKT_SEC_STK C ");
    sql.append("    JOIN ");
    sql.append("REC_STK S ON C.STK_ID = S.STK_ID ");
    sql.append("WHERE ");
    sql.append("S.REC_RISK <= :memberRisk ");
    sql.append("AND C.SEC_ID IN (:intSec) ");
    sql.append("AND S.REC_RTN >= :expRtn ");
    sql.append("GROUP BY ");
    sql.append("S.REC_STK_ID, ");
    sql.append("    S.STK_ID, ");
    sql.append("    C.SEC_NM, ");
    sql.append("    C.STK_NM, ");
    sql.append("    S.REC_RTN, ");
    sql.append("    S.REC_VOL, ");
    sql.append("    S.REC_RISK ");
    sql.append("ORDER BY S.REC_RTN DESC ");

    int memberRisk = memberTraits.getMemberRisk();
    double expRtn = memberTraits.getExpRtn();
    // intSec를 문자열로 받고, 이를 Integer 리스트로 변환
    String intSecString = String.join(",", memberTraits.getIntSec()); // 예: "31,59"
    List<Integer> intSec = Arrays.stream(intSecString.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk",memberRisk)
        .addValue("expRtn",expRtn)
        .addValue("intSec",intSec);

    log.info("Member Risk: {}", memberRisk);
    log.info("Expected Return: {}", expRtn);
    log.info("Interest Sector IDs: {}", intSec);


    List<RecStk> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RecStk.class));
    return list;
  }

  @Override
  public List<RecStk> listWithoutTraitSector(HttpServletRequest request) {

    // 성향 정보 불러오기
    MemberTraits memberTraits = getMemberTraits(request);

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT DISTINCT ");
    sql.append("S.REC_STK_ID, ");
    sql.append("    C.SEC_NM, ");
    sql.append("    C.STK_NM, ");
    sql.append("    S.REC_RTN, ");
    sql.append("    S.REC_VOL, ");
    sql.append("    S.REC_RISK ");
    sql.append("FROM ");
    sql.append("MKT_SEC_STK C ");
    sql.append("    JOIN ");
    sql.append("REC_STK S ON C.STK_ID = S.STK_ID ");
    sql.append("WHERE ");
    sql.append("S.REC_RISK <= :memberRisk ");
    sql.append("AND S.REC_RTN >= :expRtn ");
    sql.append("GROUP BY ");
    sql.append("S.REC_STK_ID, ");
    sql.append("    S.STK_ID, ");
    sql.append("    C.SEC_NM, ");
    sql.append("    C.STK_NM, ");
    sql.append("    S.REC_RTN, ");
    sql.append("    S.REC_VOL, ");
    sql.append("    S.REC_RISK ");
    sql.append("ORDER BY S.REC_RTN DESC ");

    int memberRisk = memberTraits.getMemberRisk();
    double expRtn = memberTraits.getExpRtn();

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk",memberRisk)
        .addValue("expRtn",expRtn);

    log.info("Member Risk: {}", memberRisk);
    log.info("Expected Return: {}", expRtn);

    List<RecStk> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RecStk.class));
    return list;


  }

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
}
