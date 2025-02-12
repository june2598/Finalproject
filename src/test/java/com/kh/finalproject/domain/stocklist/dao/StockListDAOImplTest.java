package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.StockListDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest

class StockListDAOImplTest {

  @Autowired
  StockListDAO stockListDAO;


  @Test
  void getStockList() {
    int marketId = 1;   // 시장
    String orderBy = "r.MARCAP";  // 정렬기준
    int risk = 3;     // 위험도 범위 설정
    int offset = 10;   // 오프셋
    List<StockListDto> stockList = stockListDAO.getStockList(marketId, orderBy, risk, offset);
    for (StockListDto stocks : stockList) {
      log.info("stocks={}", stocks);
    }
  }
}