package com.kh.finalproject.domain.member.dao;

import com.kh.finalproject.domain.entity.Member;
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

  @Override
  public Optional<String> findMemberIdByEmail(String email) {
    StringBuffer sql = new StringBuffer();
    sql.append(" select member_id ");
    sql.append(" from member ");
    sql.append(" where email =  :email ");

    Map<String, String> param = Map.of("email", email);
    try{
      Member member = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Member.class));
      return Optional.ofNullable(member != null ? member.getMemberId() : null);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> findPw(String email, String memberId) {
    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT PW ");
    sql.append(" FROM MEMBER ");
    sql.append(" WHERE MEMBER_ID = :memberId AND EMAIL = :email ");

    Map<String, String> param = Map.of("email", email, "memberId", memberId);
    try{
      Member member = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Member.class));
      return Optional.ofNullable(member != null ? member.getPw() : null);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public int updateById(Long memberSeq, Member member) {
    StringBuffer sql = new StringBuffer();

    sql.append(" UPDATE MEMBER ");
    sql.append(" SET PW = :PW, TEL= :TEL, EMAIL=:EMAIL, UDATE=SYSDATE ");
    sql.append(" WHERE MEMBER_SEQ = :MEMBER_SEQ ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("pw", member.getPw())
        .addValue("tel", member.getTel())
        .addValue("email", member.getEmail())
        .addValue("memberSeq",memberSeq);

    int rows = template.update(sql.toString(), param);
    return rows;
  }
}
