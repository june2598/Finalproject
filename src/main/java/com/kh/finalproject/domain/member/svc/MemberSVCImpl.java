package com.kh.finalproject.domain.member.svc;

import com.kh.finalproject.domain.emailauth.svc.EmailAuthSVC;
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
  private final EmailAuthSVC emailAuthSVC;

  @Override
  public Member join(Member member, String code) {
    //이메일 인증 확인
    boolean isValid = emailAuthSVC.validateCode(member.getEmail(), code);
    if(!isValid){
      throw new IllegalArgumentException("이메일 인증이 필요합니다.");
    }
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

  @Override
  public boolean isValidMemberId(String memberId) {

    return memberId.matches("^[a-zA-Z][a-zA-Z0-9]{0,14}$");
  }

  @Override
  public boolean isValidPassword(String password) {
    return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}:<>?])[A-Za-z\\d!@#$%^&*()_+{}:<>?]{8,15}$");
  }

  @Override
  public Optional<String> findMemberIdByEmail(String email) {
    return memberDAO.findMemberIdByEmail(email);
  }
}
