package com.kh.finalproject.domain.member.svc;

import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.dao.MemberDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSVCImpl implements MemberSVC{
  private final MemberDAO memberDAO;

  @Override
  public Member join(Member member) {
    return memberDAO.insertMember(member);
  }

  @Override
  public boolean isMember(String memberId) {
    return memberDAO.isExist(memberId);
  }

  @Override
  public Optional<Member> findByMemberSeq(Long memberSeq) {
    return memberDAO.findByMemberSeq(memberSeq);
  }

  @Override
  public Optional<Member> findByMemberId(String memberId) {
    return memberDAO.findByMemberId(memberId);
  }
}
