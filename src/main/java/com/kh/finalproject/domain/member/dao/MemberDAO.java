package com.kh.finalproject.domain.member.dao;

import com.kh.finalproject.domain.entity.Member;

import java.util.Optional;

public interface MemberDAO {

  //가입
  Member insertMember(Member member);

  //회원 존재 유무
  boolean isExist(String memberId);

  //회원 조회
  Optional<Member> findByMemberSeq(Long memberSeq);
  Optional<Member> findByMemberId(String memberId);

  // 회원 id 찾기
  Optional<String> findMemberIdByEmail(String email);

  // 회원 비밀번호 찾기
  Optional<String> findPw(String email, String memberId);

  // 회원 정보 수정
  int updateById(Long memberSeq, Member member);
}
