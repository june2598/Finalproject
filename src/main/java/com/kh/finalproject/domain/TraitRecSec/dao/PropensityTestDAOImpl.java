package com.kh.finalproject.domain.TraitRecSec.dao;

import com.kh.finalproject.domain.dto.MemberTraits;
import com.kh.finalproject.domain.vo.TraitRecSec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PropensityTestDAOImpl implements PropensityTestDAO {
  private final NamedParameterJdbcTemplate template;

  @Override
  public Long save(com.kh.finalproject.domain.entity.MemberTraits memberTraits) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO MEMBER_TRAITS(TRAIT_ID, MEMBER_SEQ, MEMBER_RISK, INT_SEC, EXP_RTN) ");
    sql.append("VALUES(member_traits_seq.nextval, :memberSeq, :memberRisk, :intSec, :expRtn) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(memberTraits);
    KeyHolder keyholder = new GeneratedKeyHolder();
    long rows = template.update(sql.toString(), param, keyholder, new String[]{"trait_id"});
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

  // 성향 업종 리스트 불러오기 (위험도 선택)
  @Override
  public List<TraitRecSec> listAll(int memberRisk) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT * ");
    sql.append(" FROM TRAIT_REC_SEC ");
    sql.append(" WHERE TRAIT_SEC_RISK <= :memberRisk ");
    sql.append(" ORDER BY IS_REC DESC ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberRisk", memberRisk);

    List<TraitRecSec> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(TraitRecSec.class));
    return list;
  }

  // 고객 성향 정보를 조회
  @Override
  public Optional<MemberTraits> findById(Long memberSeq) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT m.MEMBER_SEQ, m.MEMBER_ID, t.MEMBER_RISK, t.INT_SEC, t.EXP_RTN ");
    sql.append(" FROM MEMBER m ");
    sql.append(" JOIN MEMBER_TRAITS t ON m.member_seq = t.member_seq ");
    sql.append(" WHERE m.MEMBER_SEQ = :memberSeq ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberSeq", memberSeq);

    MemberTraits memberTraits = null;
    try {
      memberTraits = template.queryForObject(
          sql.toString(),
          param,
          BeanPropertyRowMapper.newInstance(MemberTraits.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
    return Optional.of(memberTraits);
  }
}
