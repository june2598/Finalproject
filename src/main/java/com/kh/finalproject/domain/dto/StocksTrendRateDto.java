package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class StocksTrendRateDto {
  private String stkNm;
  private double newsIncreaseRate;
  private double communityIncreaseRate;
}
