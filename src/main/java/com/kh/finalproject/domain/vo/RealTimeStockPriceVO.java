package com.kh.finalproject.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class RealTimeStockPriceVO {
  private String stkNm;         // 종목명
  private Double change;        // 전일비
  private Double changeRatio;   // 등락률
  private LocalDateTime cdate;  // 날짜
}
