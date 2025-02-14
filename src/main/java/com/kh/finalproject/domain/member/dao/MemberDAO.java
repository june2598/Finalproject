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
}
