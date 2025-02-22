package com.kh.finalproject.domain.member.svc;

import com.kh.finalproject.domain.emailauth.svc.EmailAuthSVC;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.dao.MemberDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSVCImpl implements MemberSVC{

  @Autowired
  private JavaMailSender mailSender;

  private final MemberDAO memberDAO;
  private final EmailAuthSVC emailAuthSVC;
  private final BCryptPasswordEncoder passwordEncoder;

  private String generateTempPassword(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    StringBuilder tempPassword = new StringBuilder();
    SecureRandom random = new SecureRandom();

    for (int i = 0; i < length; i++) {
      tempPassword.append(chars.charAt(random.nextInt(chars.length())));
    }

    return tempPassword.toString();
  }

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
  public boolean isValidTel(String tel) {
    return tel.matches("^\\d{10,11}$");
  }

  @Override
  public Optional<String> findMemberIdByEmail(String email) {
    return memberDAO.findMemberIdByEmail(email);
  }

  @Override
  @Transactional
  public void sendFindPwToEmail(String email, String memberId) {

    Optional<String> optionalPw = memberDAO.findPw(email,memberId);

    if (optionalPw.isPresent()) {
      //  임시 비밀번호 생성
      String tempPassword = generateTempPassword(10);
      log.info("🔹 생성된 임시 비밀번호: {}", tempPassword);

      // 임시 비밀번호 해싱 후 DB에 저장
      String hashedPassword = passwordEncoder.encode(tempPassword);
      memberDAO.updatePassword(memberId, hashedPassword);
      log.info("🔹 해싱된 임시 비밀번호 저장 완료");

      // 3. 이메일 전송
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("임시 비밀번호 안내");
      message.setText("회원님의 임시 비밀번호: " + tempPassword + "\n로그인 후 비밀번호를 변경해주세요.");
      mailSender.send(message);

      log.info("🔹 임시 비밀번호 이메일 전송 완료");

    } else {
      log.warn("비밀번호를 찾을 수 없음 (이메일 또는 회원 ID 불일치)");
    }
  }

  @Override
  public int updateById(Long memberSeq, Member member) {
    return memberDAO.updateById(memberSeq,member);
  }

  @Override
  public int updatePassword(String memberId, String hashedPassword) {
    return memberDAO.updatePassword(memberId,hashedPassword);
  }
}


