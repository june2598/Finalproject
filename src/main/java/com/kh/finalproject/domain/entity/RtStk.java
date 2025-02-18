package com.kh.finalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString

//RealTime-Stocks-Data

public class RtStk {
  private Long rtStkId;                  //  RT_STK_ID	NUMBER(20,0)
  private Long stkId;                    //  STK_ID	NUMBER(10,0)
  private Long price;                    //  PRICE	NUMBER(10,0)
  private Long change;                   //  CHANGE	NUMBER(10,0)
  private Double changeRatio;            //  CHANGE_RATIO	NUMBER(6,2)
  private Long amount;                   //  AMOUNT	NUMBER(20,0)
  private Long volume;                   //  VOLUME	NUMBER(20,0)
  private Long marcap;                   //  MARCAP	NUMBER(20,0)
  private LocalDateTime cdate;           //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;           //  UDATE	TIMESTAMP(6)
}
