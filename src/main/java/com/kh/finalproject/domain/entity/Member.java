package com.kh.finalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Member {
  private Long memberSeq;                 //  MEMBER_SEQ NUMBER(10)
  private String memberId;                //  MEMBER_ID VARCHAR2(16)
  private String pw;                      //  PW VARCHAR2(20)
  private String tel;                     //  TEL VARCHAR2(11)
  private String memberClsfc;             //  MEMBER_CLSFC VARCHAR2(20)
  private String email;                   //  EMAIL VARCHAR2(40)
  private LocalDateTime cdate;            //  CDATE TIMESTAMP
  private LocalDateTime udate;            //  UDATE TIMESTAMP
}
