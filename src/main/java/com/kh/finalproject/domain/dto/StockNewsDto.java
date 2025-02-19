package com.kh.finalproject.domain.dto;

import java.time.LocalDateTime;

public class StockNewsDto {
  private String title;                 // 뉴스 기사 제목
  private Long StkId;                   // 종목 ID
  private String mediaName;             // 언론사
  private String url;                   // 뉴스 링크
  private LocalDateTime publishedDate;  // 기사 제공 날짜
}
