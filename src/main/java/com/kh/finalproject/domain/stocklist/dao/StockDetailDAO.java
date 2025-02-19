package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;

import java.util.List;

public interface StockDetailDAO {

  // 특정 종목 관련 뉴스 가져오기
  List<StockNewsDto> getStockNewsList (Long stkId);

  // 특정 종목의 상세 지표 테이블 가져오기 (당일)
  List<StockListDto> getStockDetail (String stkCode);

  // 종목 코드로 종목 id 가져오기

  Long getStkIdByStkCode (String stkCode);

  // 종목 코드로 종목명 가져오기

  String getStkNmByStkCode (String stkCode);



}
