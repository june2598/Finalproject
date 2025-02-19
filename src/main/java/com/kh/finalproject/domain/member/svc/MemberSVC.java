package com.kh.finalproject.domain.member.svc;

import com.kh.finalproject.domain.entity.Member;

import java.util.Optional;

public interface MemberSVC {
  //가입
  Member join(Member member, String code);

  //회원 존재 유무
  boolean isMember(String memberId);

  // 아이디 유효성 검증
  boolean isValidMemberId(String memberId);

  // 비밀번호 유효성 검증
  boolean isValidPassword(String password);

  //회원 조회
  Optional<Member> findByMemberSeq(Long memberSeq);
  Optional<Member> findByMemberId(String memberId);

  // 이메일로 회원 아이디 찾기
  Optional<String> findMemberIdByEmail(String email);

  // 가입 이메일로 찾은 비밀번호 전송
  void sendFindPwToEmail(String email, String memberId);

  // 회원 정보 수정
  int updateById(Long memberSeq, Member member);

  public int updatePassword(String memberId, String hashedPassword);

}
