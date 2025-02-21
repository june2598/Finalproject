package com.kh.finalproject.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class RealTimeStockVolumeVO {
  private String stkNm;
  private String stkCode;
  private Long changeVolume;
  private Double changeRatioVolume;
  private LocalDateTime cdate;
}
