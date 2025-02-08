package com.kh.finalproject.domain.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TraitStk {
  private Long traitStkId;              //  TRAIT_STK_ID	NUMBER(10,0)
  private Long stkId;                   //  STK_ID	NUMBER(10,0)
  private double traitStkRtn;           //  TRAIT_STK_RTN	NUMBER(5,2)
  private double traitStkVol;           //  TRAIT_STK_VOL	NUMBER(5,2)
  private int traitStkRisk;             //  TRAIT_STK_RISK	NUMBER(1,0)
  private LocalDateTime cdate;          //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;          //  UDATE	TIMESTAMP(6)
}
