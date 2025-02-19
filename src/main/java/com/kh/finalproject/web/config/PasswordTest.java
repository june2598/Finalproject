package com.kh.finalproject.web.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    String rawPassword = "Rla81680!"; // 로그인 시 입력한 비밀번호
    String storedHashedPassword = "$2a$10$3LS/wjOMT7V4VvNqAi2c..FO2.4.xM0Ku52CBZFH9.QBKEd1cUg9C"; // DB에 저장된 비밀번호

    boolean isMatch = encoder.matches(rawPassword, storedHashedPassword);

    System.out.println("비밀번호 비교 결과: " + isMatch);
  }
}
