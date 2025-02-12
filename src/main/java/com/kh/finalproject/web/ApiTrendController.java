package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.SectorsTrendRateDto;
import com.kh.finalproject.domain.dto.StocksTrendRateDto;
import com.kh.finalproject.domain.trend.svc.TrendSVC;
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

@RestController
@Slf4j
@RequestMapping("/api/trend")
@RequiredArgsConstructor

public class ApiTrendController {

  private final TrendSVC trendSVC;

  //실시간 업종 트랜드 목록(default : 코스피 시장 뉴스기사 순위 정렬)
  @GetMapping //(produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<List<SectorsTrendRateDto>> sectorTrend(
      @RequestParam(value = "marketId", defaultValue = "1") Integer marketId,
      @RequestParam(value = "orderBy", defaultValue = "NEWS_INCREASE_RATE") String orderBy
      ) {

    log.info("Fetching sector trend for marketId: {}, orderBy: {}", marketId, orderBy);

    ApiResponse<List<SectorsTrendRateDto>> res = null;
    List<SectorsTrendRateDto> sectorTrendList = trendSVC.getSectorTrend(marketId,orderBy);

    if (sectorTrendList.size() != 0) {
      res = ApiResponse.of(ApiResponseCode.SUCCESS, sectorTrendList);
    } else {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND,null);
    }
    return res;
  }

  @GetMapping("/stocks")
  public ApiResponse<List<StocksTrendRateDto>> stocksTrend(
      @RequestParam(value = "orderBy", defaultValue = "NEWS_INCREASE_RATE") String orderBy) {

    log.info("Fetching sector trend for orderBy: {}", orderBy);

    ApiResponse<List<StocksTrendRateDto>> res = null;
    List<StocksTrendRateDto> stocksTrendList = trendSVC.getStocksTrend(orderBy);

    if (stocksTrendList.size() != 0) {
      res = ApiResponse.of(ApiResponseCode.SUCCESS, stocksTrendList);
    } else {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND,null);
    }
    return res;
  }

}
