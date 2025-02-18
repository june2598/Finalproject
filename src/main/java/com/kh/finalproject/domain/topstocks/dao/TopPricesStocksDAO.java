package com.kh.finalproject.domain.topstocks.dao;

import com.kh.finalproject.domain.vo.RealTimeStockPriceVO;

import java.util.List;

public interface TopPricesStocksDAO {

  // 실시간 주가 상위 5종목
  List<RealTimeStockPriceVO> findTop5ByPrice(String orderBy);

  // 실시간 주가 상위 5종목 전일비 정렬
  List<RealTimeStockPriceVO> findTop5ByChangePrice();

  // 실시간 주가 상위 5종목 등락률 정렬
  List<RealTimeStockPriceVO> findTop5ByChangeRatioPrice();

}
