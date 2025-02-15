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
}
