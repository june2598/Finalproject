package com.kh.finalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TraitSec {
  private Long traitSecId;                  //  TRAIT_SEC_ID	NUMBER(10,0)
  private Long secId;                       //  SEC_ID	NUMBER(6,0)
  private double traitSecRtn;               //  TRAIT_SEC_RTN	NUMBER(5,2)
  private double traitSecVol;               //  TRAIT_SEC_VOL	NUMBER(5,2)
  private double traitSecRisk;              //  TRAIT_SEC_RISK	NUMBER(3,2)
  private LocalDateTime cdate;              //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;              //  UDATE	TIMESTAMP(6)
}
