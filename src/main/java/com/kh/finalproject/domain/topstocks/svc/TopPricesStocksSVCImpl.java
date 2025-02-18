package com.kh.finalproject.domain.topstocks.svc;

import com.kh.finalproject.domain.topstocks.dao.TopPricesStocksDAO;
import com.kh.finalproject.domain.vo.RealTimeStockPriceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class TopPricesStocksSVCImpl implements TopPricesStocksSVC {

  private final TopPricesStocksDAO topPricesStocksDAO;

  @Override
  public List<RealTimeStockPriceVO> findTop5ByPrice(String orderBy) {
    return topPricesStocksDAO.findTop5ByPrice(orderBy);
  }

  @Override
  public List<RealTimeStockPriceVO> findTop5ByChangePrice() {
    return topPricesStocksDAO.findTop5ByChangePrice();
  }

  @Override
  public List<RealTimeStockPriceVO> findTop5ByChangeRatioPrice() {
    return topPricesStocksDAO.findTop5ByChangeRatioPrice();
  }
}
