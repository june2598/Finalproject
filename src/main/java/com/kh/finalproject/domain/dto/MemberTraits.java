package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberTraits {
  private Long memberSeq;      // MEMBER_SEQ
  private String memberId;       // MEMBER_ID
  private int memberRisk;        // MEMBER_RISK
  private String intSec;         // INT_SEC
  private double expRtn;         // EXP_RTN
}
