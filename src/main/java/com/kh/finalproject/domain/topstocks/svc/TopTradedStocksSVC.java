package com.kh.finalproject.domain.topstocks.svc;

import com.kh.finalproject.domain.vo.RealTimeStockVolumeVO;

import java.util.List;

public interface TopTradedStocksSVC {

  // 실시간 거래량 상위 5종목 정렬
  List<RealTimeStockVolumeVO> findTop5ByVolume(String orderBy);

  // Volume의 차이 기준 정렬
  List<RealTimeStockVolumeVO> findTop5ByChangeVolume();

  // 변화비율 기준 정렬
  List<RealTimeStockVolumeVO> findTop5ByChangeRatioVolume();
}
