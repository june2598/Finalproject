package com.kh.finalproject.domain.trend.svc;

import com.kh.finalproject.domain.dto.SectorsTrendRateDto;
import com.kh.finalproject.domain.dto.StocksTrendRateDto;
import com.kh.finalproject.domain.trend.dao.TrendDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class TrendSVCImpl implements TrendSVC {

  private final TrendDAO trendDAO;

  @Override
  public List<SectorsTrendRateDto> sectorTrendByNews(int marketId) {

    return trendDAO.sectorTrendByNews(marketId);
  }

  @Override
  public List<SectorsTrendRateDto> sectorTrendByCommunity(int marketId) {
    return trendDAO.sectorTrendByCommunity(marketId);
  }

  @Override
  public List<SectorsTrendRateDto> getSectorTrend(int marketId, String orderBy) {
    return trendDAO.getSectorTrend(marketId,orderBy);
  }

  @Override
  public List<StocksTrendRateDto> getStocksTrend(String orderBy) {
    return trendDAO.getStocksTrend(orderBy);
  }

  @Override
  public List<StocksTrendRateDto> stocksTrendByNews() {
    return trendDAO.stocksTrendByNews();
  }

  @Override
  public List<StocksTrendRateDto> stocksTrendByCommunity() {
    return trendDAO.stocksTrendByCommunity();
  }
}
