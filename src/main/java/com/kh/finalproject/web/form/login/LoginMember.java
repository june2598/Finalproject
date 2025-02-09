package com.kh.finalproject.web.form.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LoginMember {
  private Long memberSeq;       //  내부관리용 시퀀스
  private String memberId;      //  회원 로그인 아이디
  private String email;         //  회원 이메일
  private String memberClsfc;   //  회원 유형

}
