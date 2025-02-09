package com.kh.finalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MemberTraits {
  private Long traitId;                //  TRAIT_ID	NUMBER(10,0)
  private String memberSeq;                //  MEMBER_SEQ	VARCHAR2(15 BYTE)
  private int memberRisk;              //  MEMBER_RISK	NUMBER(1,0)
  private String intSec;              //  INT_SEC	VARCHAR2(19 BYTE)
  private double expRtn;              //  EXP_RTN	NUMBER(3,1)
  private LocalDateTime cdate;              //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;              //  UDATE	TIMESTAMP(6)
}
