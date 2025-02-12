package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.stocklist.dao.StockListDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class StockListSVCImpl implements StockListSVC {

  private final StockListDAO stockListDAO;

  @Override
  public List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset) {

    return stockListDAO.getStockList(marketId, orderBy, risk, offset);
  }
}
