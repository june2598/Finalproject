package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class StockNewsDto {
  private String title;                 // 뉴스 기사 제목
  private Long stkId;                   // 종목 ID
  private String stkCode;
  private String mediaName;             // 언론사
  private String newsUrl;                   // 뉴스 링크
  private LocalDateTime publishedDate;  // 기사 제공 날짜
}
