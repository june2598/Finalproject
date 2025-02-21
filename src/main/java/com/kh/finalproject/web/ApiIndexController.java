package com.kh.finalproject.web;

import com.kh.finalproject.domain.topstocks.svc.TopPricesStocksSVC;
import com.kh.finalproject.domain.topstocks.svc.TopTradedStocksSVC;
import com.kh.finalproject.domain.trend.svc.TrendSVC;
import com.kh.finalproject.domain.vo.RealTimeStockPriceVO;
import com.kh.finalproject.domain.vo.RealTimeStockVolumeVO;
import com.kh.finalproject.web.api.ApiResponse;
import com.kh.finalproject.web.api.ApiResponseCode;
import com.kh.finalproject.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor

public class ApiIndexController {

  private final TopPricesStocksSVC topPricesStocksSVC;
  private final TopTradedStocksSVC topTradedStocksSVC;
  private final TrendSVC trendSVC;

  @GetMapping ("/prices")
  public ApiResponse<List<RealTimeStockPriceVO>> realTimePrice (
      @RequestParam(value = "orderBy", defaultValue = "CHANGE") String orderBy) {

    ApiResponse<List<RealTimeStockPriceVO>> res = null;
    List<RealTimeStockPriceVO> realTimeStockPriceList = topPricesStocksSVC.findTop5ByPrice(orderBy);

    if (realTimeStockPriceList.size() != 0) {
      res = ApiResponse.of(ApiResponseCode.SUCCESS, realTimeStockPriceList);
    } else {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND,null);
    }
    return res;
  }

  @GetMapping ("/volume")
  public ApiResponse<List<RealTimeStockVolumeVO>> realTimeVolume (
      @RequestParam(value = "orderBy", defaultValue = "CHANGE") String orderBy) {

    ApiResponse<List<RealTimeStockVolumeVO>> res = null;
    List<RealTimeStockVolumeVO> realTimeStockVolumeList = topTradedStocksSVC.findTop5ByVolume(orderBy);

    if (realTimeStockVolumeList.size() != 0) {
      res = ApiResponse.of(ApiResponseCode.SUCCESS, realTimeStockVolumeList);
    } else {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND,null);
    }
    return res;
  }

//  @GetMapping ("/indices")
//  public ApiResponse<List<DomesticIndicesVO>> domesticIndices() {
//
//    ApiResponse<List<DomesticIndicesVO>> res = null;
//    List<DomesticIndicesVO> domesticIndicesVOList = trendSVC.getDomesticIndices();
//
//    if (domesticIndicesVOList.size() != 0) {
//      res = ApiResponse.of(ApiResponseCode.SUCCESS, domesticIndicesVOList);
//    } else {
//      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND,null);
//    }
//    return res;
//  }

}
