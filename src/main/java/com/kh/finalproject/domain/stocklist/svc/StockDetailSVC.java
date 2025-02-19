package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;

import java.util.List;

public interface StockDetailSVC {

  List<StockNewsDto> getStockNewsList (Long stkId);

  List<StockListDto> getStockDetail (String stkCode);

  Long getStkIdByStkCode (String stkCode);

  String getStkNmByStkCode (String stkCode);

}
