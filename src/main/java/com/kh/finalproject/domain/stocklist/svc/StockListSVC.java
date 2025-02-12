package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.StockListDto;

import java.util.List;

public interface StockListSVC {
  List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset);
}
