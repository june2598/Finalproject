package com.kh.finalproject.domain.trend.dao;

import com.kh.finalproject.domain.dto.SectorsTrendRateDto;
import com.kh.finalproject.domain.dto.StocksTrendRateDto;

import java.util.List;

public interface TrendDAO {

  // 시장별 뉴스수, 커뮤니티수 증가 업종순위 (뉴스기사 증가율 기준 정렬)
  List<SectorsTrendRateDto> sectorTrendByNews(int marketId);

  // 시장별 뉴스수, 커뮤니티수 증가 업종순위 (커뮤니티 증가율 기준 정렬)
  List<SectorsTrendRateDto> sectorTrendByCommunity(int marketId);

  // 시장별 뉴스수, 커뮤니티수 증가 업종순위
  List<SectorsTrendRateDto> getSectorTrend(int marketId, String orderBy);

  // 실시간 이슈 종목 순위 (뉴스기사수, 커뮤니티 증가수)
  List<StocksTrendRateDto> getStocksTrend(String orderBy);

  // 이슈종목 뉴스 정렬
  List<StocksTrendRateDto> stocksTrendByNews();

  // 이슈종목 커뮤니티 정렬
  List<StocksTrendRateDto> stocksTrendByCommunity();

  // 메인페이지에 띄울 주요 단어 다섯개





}
