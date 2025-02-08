package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

// 실제로 출력해야되는 영역
public class MemberTraitsDto {
  private Long memberSeq;        // MEMBER_SEQ 회원시퀀스
  private String memberId;       // MEMBER_ID 회원 아이디
  private int memberRisk;        // MEMBER_RISK 위험단계
  private String intSec;         // INT_SEC 관심업종
  private double expRtn;         // EXP_RTN 희망 수익률(%)
}
