package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class MemberInfoDto {
  private Long memberSeq;
  private String memberId;
  private String pw;
  private String tel;
  private String email;
}
