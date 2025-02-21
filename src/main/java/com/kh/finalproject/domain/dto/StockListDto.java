package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StockListDto {
  private String stkNm;           // 종목명
  private String stkCode;         // 종목코드
  private Long secId;             // 업종ID
  private String secNm;           // 업종명
  private Long price;             // 가격
  private Long change;            // 전일비
  private double changeRatio;     // 등락률
  private Long volume;            // 거래량
  private String amount;          // 거래대금
  private String marcap;          // 시가총액
  private int traitStkRisk;       // 위험도(한달기준-성향쪽에서 가져옴)
}
