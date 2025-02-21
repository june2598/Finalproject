package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.SectorListDto;
import com.kh.finalproject.domain.dto.StockListDto;

import java.util.List;

public interface StockListDAO {

  // 종목 리스트 조회 (업종 선택 가능)
  List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset, Long secId);

  // 업종 리스트 조회 (시장 선택 가능)
  List<SectorListDto> getSectorList(int marketId);

}
