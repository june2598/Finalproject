package com.kh.finalproject.domain.emailauth.svc;

public interface EmailAuthSVC {

  void sendVerificationEmail(String email);

  boolean validateCode(String email, String code);




}
