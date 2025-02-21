package com.kh.finalproject.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class DomesticIndicesVO {
  private Long marketId;          // 시장 id
  private Double indexValue;      // 시장 지수
  private Double indexComp;       // 전일비
  private Double changeRatio;     // 등락률
  private LocalDateTime cdate;    // 날짜

}
