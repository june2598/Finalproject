package com.kh.finalproject.domain.propertytest.dao;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PropensityTestDAOImpl implements PropensityTestDAO {
  private final NamedParameterJdbcTemplate template;


  // 성향 정보 저장
  @Override
  public Long save(MemberTraits memberTraits) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO MEMBER_TRAITS(TRAIT_ID, MEMBER_SEQ, MEMBER_RISK, INT_SEC, EXP_RTN) ");
    sql.append("VALUES(member_traits_seq.nextval, :memberSeq, :memberRisk, :intSec, :expRtn) ");

    //intSec을 쉼표로 구분된 문자열로 변환
    String intSecString = String.join(",", memberTraits.getIntSec());

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberSeq",memberTraits.getMemberSeq())
        .addValue("memberRisk",memberTraits.getMemberRisk())
        .addValue("intSec",intSecString)
        .addValue("expRtn",memberTraits.getExpRtn());

    KeyHolder keyholder = new GeneratedKeyHolder();
    template.update(sql.toString(), param, keyholder, new String[]{"trait_id"});
    Number tidNumber = (Number) keyholder.getKeys().get("trait_id");
    long tid = tidNumber.longValue();
    return tid;
  }


  // 성향 업종 리스트 전체 불러오기
  @Override
  public List<TraitRecSec> listAll() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT * ");
    sql.append(" FROM TRAIT_REC_SEC ");
    sql.append(" ORDER BY IS_REC DESC ");

    SqlParameterSource param = new MapSqlParameterSource();

    List<TraitRecSec> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(TraitRecSec.class));
    return list;
  }

  // 성향 업종 리스트 불러오기 (위험도 선택, 시장별 정렬)
  @Override
  public List<TraitRecSec> listAll(int memberRisk) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT DISTINCT ");
    sql.append(" trs.TRAIT_REC_SEC_ID, ");
    sql.append(" trs.SEC_ID, ");
    sql.append(" trs.TRAIT_REC_SEC_RISK, ");
    sql.append(" m.SEC_NM, ");
    sql.append(" m.MARKET_ID, ");
    sql.append(" trs.IS_REC, ");
    sql.append(" trs.TRAIT_REC_SEC_RTN ");
    sql.append(" FROM TRAIT_REC_SEC trs ");
    sql.append(" JOIN MKT_SEC_STK m ON trs.SEC_ID = m.SEC_ID ");
    sql.append(" WHERE trs.TRAIT_REC_SEC_RISK <= :memberRisk ");
    sql.append(" ORDER BY ");
    sql.append(" m.MARKET_ID ASC, ");
    sql.append(" trs.IS_REC DESC, ");
    sql.append(" trs.TRAIT_REC_SEC_RTN DESC ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk", memberRisk);

    List<TraitRecSec> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(TraitRecSec.class));
    return list;
  }

  // 고객 성향 정보를 조회
  @Override
  public Optional<MemberTraitsDto> findById(Long memberSeq) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT m.MEMBER_SEQ, m.MEMBER_ID, t.MEMBER_RISK, t.INT_SEC, t.EXP_RTN ");
    sql.append(" FROM MEMBER m ");
    sql.append(" JOIN MEMBER_TRAITS t ON m.member_seq = t.member_seq ");
    sql.append(" WHERE m.MEMBER_SEQ = :memberSeq ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberSeq", memberSeq);

    MemberTraitsDto memberTraitsDto = null;
    try {
      memberTraitsDto = template.queryForObject(
          sql.toString(),
          param,
          BeanPropertyRowMapper.newInstance(MemberTraitsDto.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
    return Optional.of(memberTraitsDto);
  }

  //관심 업종 없을때 희망수익률 최대치 조회
  @Override
  public Optional<Double> findMaxRtn(int memberRisk) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT TRAIT_STK_RTN ");
    sql.append(" FROM ( ");
    sql.append("     SELECT t.TRAIT_STK_RTN ");
    sql.append("     FROM TRAIT_STK t ");
    sql.append("     WHERE t.TRAIT_STK_RISK <= :memberRisk ");
    sql.append(" ORDER BY t.TRAIT_STK_RTN DESC ");
    sql.append(" ) ");
    sql.append(" WHERE ROWNUM = 1 ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk",memberRisk);

    Double maxRtn = template.queryForObject(sql.toString(), param, Double.class);
    return Optional.ofNullable(maxRtn); // 결과가 null일 경우 Optional.empty() 반환
  }

  @Override
  public Optional<Double> findMaxRtn(int memberRisk, String intSec) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT TRAIT_STK_RTN ");
    sql.append(" FROM ( ");
    sql.append("     SELECT t.TRAIT_STK_ID, t.TRAIT_STK_RTN, ");
    sql.append("     ROW_NUMBER() OVER (ORDER BY t.TRAIT_STK_RTN DESC) AS rn ");
    sql.append("     FROM TRAIT_STK t ");
    sql.append("     JOIN MKT_SEC_STK m ON t.STK_ID = m.STK_ID ");
    sql.append("     WHERE m.SEC_ID IN (:intSec) ");
    sql.append("     AND t.TRAIT_STK_RISK <= :memberRisk ");
    sql.append(" ) ");
    sql.append(" WHERE rn = 1 ");

    //쉼표로 구분된 intSec를 리스트로 변환
    List<String> sectorIds = Arrays.asList(intSec.split(","));

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk",memberRisk)
        .addValue("intSec",sectorIds);

    try {
      Double maxRtn = template.queryForObject(sql.toString(), param, Double.class);
      return Optional.ofNullable(maxRtn); // 결과가 null일경우 Optional.empty
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();// 결과가 없을경우 Optional.empty() 반환
    }
  }
}
