package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.SectorListDto;
import com.kh.finalproject.domain.dto.StockListDto;

import java.util.List;

public interface StockListSVC {

  // 업종선택 안했을시 종목 리스트 불러오기
  List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset, Long secId);

  // 업종 리스트 조회 (시장 선택 가능)
  List<SectorListDto> getSectorList(int marketId);
}
