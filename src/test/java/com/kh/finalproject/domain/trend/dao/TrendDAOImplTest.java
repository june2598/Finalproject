package com.kh.finalproject.domain.trend.dao;

import com.kh.finalproject.domain.dto.SectorsTrendRateDto;
import com.kh.finalproject.domain.dto.StocksTrendRateDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest

class TrendDAOImplTest {
  @Autowired
  TrendDAO trendDAO;

  @Test
  @DisplayName("뉴스, 커뮤니티수 증가율")
  void sectorTrend() {
    List<SectorsTrendRateDto> list = trendDAO.sectorTrendByNews(2); // marketId=1 : 코스피, marketId=2 : 코스닥

    // 상위 다섯 개만 로그에 출력
    for (int i = 0; i < Math.min(5, list.size()); i++) {
      SectorsTrendRateDto sectorsTrendRateDto = list.get(i);
      log.info("sectorTrendRateDto={}", sectorsTrendRateDto);

    }
  }

  @Test
  @DisplayName("커뮤니티 증가율 내림차순 정렬")
  void sectorTrendByCommunity() {
    List<SectorsTrendRateDto> list = trendDAO.sectorTrendByCommunity(2); // marketId=1 : 코스피, marketId=2 : 코스닥

    // 상위 다섯 개만 로그에 출력
    for (int i = 0; i < Math.min(5, list.size()); i++) {
      SectorsTrendRateDto sectorsTrendRateDto = list.get(i);
      log.info("sectorTrendRateDto={}", sectorsTrendRateDto);

    }
  }

  @Test
  @DisplayName("업종별 증가율 내림차순 정렬")
  void getSectorTrend() {
    String orderBy = "COMMUNITY_INCREASE_RATE";
    List<SectorsTrendRateDto> list = trendDAO.getSectorTrend(2,orderBy);

    // 상위 다섯 개만 로그에 출력
    for (int i = 0; i < Math.min(5, list.size()); i++) {
      SectorsTrendRateDto sectorsTrendRateDto = list.get(i);
      log.info("sectorTrendRateDto={}", sectorsTrendRateDto);
    }

  }

  @Test
  void getStocksTrend() {
    String orderBy = "NEWS_INCREASE_RATE";
    List<StocksTrendRateDto> list = trendDAO.getStocksTrend(orderBy);

    // 상위 다섯 개만 로그에 출력
    for (int i = 0; i < Math.min(5, list.size()); i++) {
      StocksTrendRateDto stocksTrendRateDto = list.get(i);
      log.info("stocksTrendRateDto={}", stocksTrendRateDto);
    }
  }

//  @Test
//  void stocksTrendByNews() {
//  }
//
//  @Test
//  void stocksTrendByCommunity() {
//  }
}