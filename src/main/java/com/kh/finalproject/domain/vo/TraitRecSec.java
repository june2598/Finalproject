package com.kh.finalproject.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TraitRecSec {
  private Long traitRecSecId;                   //  TRAIT_REC_SEC_ID	NUMBER(10,0)
  private Long secId;                           //  SEC_ID	NUMBER(6,0)
  private int traitRecSecRisk;                  //  TRAIT_REC_SEC_RISK	NUMBER(1,0)
  private int isRec;                            //  IS_REC	NUMBER(1,0)
  private int marketId;                         //  MARKET_ID	NUMBER(1,0)
  private LocalDateTime cdate;                  //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;                  //  UDATE	TIMESTAMP(6)

}
