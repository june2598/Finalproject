package com.kh.finalproject.domain.emailauth.dao;

import com.kh.finalproject.domain.entity.AuthCode;

public interface EmailAuthDAO {

  AuthCode findByEmail(String email);

  void save(AuthCode authCode);

  String findPwByEmail(String email);
}
