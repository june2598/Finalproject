package com.kh.finalproject.web.form.stockRecommendation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class RecStk {

  private Long recStkId;            //  REC_STK_ID	NUMBER(10,0)
//  private Long stkId;               //  STK_ID	NUMBER(10,0)
  private String secNm;             //  SEC_NM
  private String stkNm;             //  STK_NM
  private double recRtn;            //  REC_RTN	NUMBER(5,2)
  private double recVol;            //  REC_VOL	NUMBER(5,2)
  private int recRisk;              //  REC_RISK	NUMBER(1,0)
  private LocalDateTime cdate;      //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;      //  UDATE	TIMESTAMP(6)


}
