package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.StockListDto;

import java.util.List;

public interface StockListDAO {

  List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset);
}
