package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SectorsTrendRateDto {
  public String secNm;
  public double newsIncreaseRate;
  public double communityIncreaseRate;
}
