package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class StocksTrendRateDto {
  public String stkNm;
  public double newsIncreaseRate;
  public double communityIncreaseRate;
}
