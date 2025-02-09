package com.kh.finalproject.domain.member.dao;

import com.kh.finalproject.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberDAOImpl implements MemberDAO {

  private final NamedParameterJdbcTemplate template;

  @Override
  public Member insertMember(Member member) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO MEMBER (MEMBER_SEQ, MEMBER_ID, PW, TEL, EMAIL) ");
    sql.append("VALUES (MEMBER_SEQ.NEXTVAL, :memberId, :pw, :tel, :email) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(member);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int rows = template.update(sql.toString(), param, keyHolder, new String[]{"member_seq"});

    long memberSeq = ((Number) keyHolder.getKeys().get("member_seq")).longValue();
    return findByMemberSeq(memberSeq).get();
  }

  @Override
  public boolean isExist(String memberId) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT COUNT(*) ");
    sql.append("  FROM MEMBER ");
    sql.append("  WHERE MEMBER_ID = :memberId ");
    Map<String, String> param = Map.of("memberId", memberId);
    Integer cntOfRec = template.queryForObject(sql.toString(), param, Integer.class);

    return (cntOfRec == 1) ? true : false;
  }

  @Override
  public Optional<Member> findByMemberSeq(Long memberSeq) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT MEMBER_SEQ, ");
    sql.append("    MEMBER_ID, ");
    sql.append("    PW, ");
    sql.append("    TEL, ");
    sql.append("    MEMBER_CLSFC, ");
    sql.append("    EMAIL, ");
    sql.append("    CDATE, ");
    sql.append("    UDATE ");
    sql.append("FROM MEMBER ");
    sql.append("WHERE MEMBER_SEQ = :memberSeq ");

    Map<String, Long> param = Map.of("memberSeq", memberSeq);
    try{
      Member member = template.queryForObject(
          sql.toString(), param, BeanPropertyRowMapper.newInstance(Member.class));

      return Optional.of(member);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Member> findByMemberId(String memberId) {

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT MEMBER_SEQ, ");
    sql.append("    MEMBER_ID, ");
    sql.append("    PW, ");
    sql.append("    TEL, ");
    sql.append("    MEMBER_CLSFC, ");
    sql.append("    EMAIL, ");
    sql.append("    CDATE, ");
    sql.append("    UDATE ");
    sql.append("  FROM MEMBER ");
    sql.append("  WHERE MEMBER_ID = :memberId ");

    Map<String, String> param = Map.of("memberId", memberId);
    try{
      Member member = template.queryForObject(
          sql.toString(), param, BeanPropertyRowMapper.newInstance(Member.class));

      return Optional.of(member);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }
}
