package com.kh.finalproject.domain.topstocks.svc;

import com.kh.finalproject.domain.topstocks.dao.TopTradedStocksDAO;
import com.kh.finalproject.domain.vo.RealTimeStockVolumeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class TopTradedStocksSVCImpl implements TopTradedStocksSVC {
  private final TopTradedStocksDAO topTradedStocksDAO;

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByVolume(String orderBy) {
    return topTradedStocksDAO.findTop5ByVolume(orderBy);
  }

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByChangeVolume() {
    return topTradedStocksDAO.findTop5ByChangeVolume();
  }

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByChangeRatioVolume() {
    return topTradedStocksDAO.findTop5ByChangeRatioVolume();
  }
}
