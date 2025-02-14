package com.kh.finalproject.domain.emailauth.svc;

import com.kh.finalproject.domain.emailauth.dao.EmailAuthDAO;
import com.kh.finalproject.domain.entity.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailAuthSVCImpl implements EmailAuthSVC {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailAuthDAO emailauthDAO;

  @Override

  @Transactional
  public void sendVerificationEmail(String email) {
    String code = generateAuthCode();

    // 만료 시간을 30분 후로 설정
    LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

    AuthCode authCode = new AuthCode();
    authCode.setEmail(email);
    authCode.setCode(code);
    authCode.setExpirationTime(expirationTime);

    emailauthDAO.save(authCode);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("이메일 인증 코드");
    message.setText("인증 코드: " + code);
    mailSender.send(message);
  }

  @Override
  public boolean validateCode(String email, String code) {
    AuthCode authCode = emailauthDAO.findByEmail(email);
    if (authCode != null && authCode.getCode().equals(code)) {
      LocalDateTime now = LocalDateTime.now();
      return now.isBefore(authCode.getExpirationTime()); // 현재 시간이 만료 시간 이전인지 확인
    }
    return false;
  }

  private String generateAuthCode() {
    return String.valueOf(new Random().nextInt(999999));
  }
}
