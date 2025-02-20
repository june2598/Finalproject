package com.kh.finalproject.web;


import com.kh.finalproject.domain.dto.SectorListDto;
import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;
import com.kh.finalproject.domain.stocklist.svc.StockDetailSVC;
import com.kh.finalproject.domain.stocklist.svc.StockListSVC;
import com.kh.finalproject.web.api.ApiResponse;
import com.kh.finalproject.web.api.ApiResponseCode;
import com.kh.finalproject.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/stockList")
@RequiredArgsConstructor

public class ApiStockListController {

  private final StockListSVC stockListSVC;
  private final StockDetailSVC stockDetailSVC;

  // 종목 리스트 불러오기 (업종 선택 가능)
  @GetMapping
  public ApiResponse<List<StockListDto>> stockList(
      @RequestParam(value = "marketId", defaultValue = "1") Integer marketId,
      @RequestParam(value = "orderBy", defaultValue = "MARCAP") String orderBy,
      @RequestParam(value = "risk", defaultValue = "3") Integer risk,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "secId", required = false) Long secId // 업종 ID 추가
  ) {

    log.info("Fetching stock list - marketId: {}, orderBy: {}, risk: {}, offset: {}, secId: {}",
        marketId, orderBy, risk, offset, secId);

    List<StockListDto> stockList = stockListSVC.getStockList(marketId, orderBy, risk, offset, secId);

    // 데이터가 없을 경우 페이지 초기화
    if (stockList.isEmpty()) {
      log.warn("No data found. Resetting offset to 0.");
      stockList = stockListSVC.getStockList(marketId, orderBy, risk, 0, secId);
    }

    // 데이터가 여전히 없으면 예외 발생
    if (stockList.isEmpty()) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }

    return ApiResponse.of(ApiResponseCode.SUCCESS, stockList);
  }

  @GetMapping("/sectorList")
  public ApiResponse<List<SectorListDto>> sectorList (
      @RequestParam(value = "marketId", defaultValue = "1") Integer marketId
  ) {

    List<SectorListDto> sectorList = stockListSVC.getSectorList(marketId);

    // 업종 데이터가 없을 경우 예외 처리
    if (sectorList.isEmpty()) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }

    return ApiResponse.of(ApiResponseCode.SUCCESS, sectorList);

  }



  // 종목별 뉴스 리스트 불러오기
  @GetMapping("/{stkCode}/news")
  public ApiResponse<List<StockNewsDto>> getStockNewsList(
      @PathVariable("stkCode") String stkCode) {

    log.info("Fetching stock news for stockCode: {}", stkCode);

    Long stkId = stockDetailSVC.getStkIdByStkCode(stkCode);

    ApiResponse<List<StockNewsDto>> res = null;

    if (stkId == null || stkId <= 0) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }
    List<StockNewsDto> stockNewsList = stockDetailSVC.getStockNewsList(stkId);
    res = ApiResponse.of(ApiResponseCode.SUCCESS, stockNewsList);
    return res;
  }

  // 종목별 상세 지표 불러오기
  @GetMapping("/{stkCode}/detail")
  public ApiResponse<List<StockListDto>> getStockDetail(
      @PathVariable("stkCode") String stkCode) {

    log.info("Fetching stock detail for stockCode: {}", stkCode);

    ApiResponse<List<StockListDto>> res = null;

    if (stkCode == null) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }
    List<StockListDto> stockDetail = stockDetailSVC.getStockDetail(stkCode);
    res = ApiResponse.of(ApiResponseCode.SUCCESS,stockDetail);
    return res;

  }

  // 종목명을 종목 코드로 변환하는 API
  @GetMapping("/convert")
  public ApiResponse<Map<String, String>> convertStockNameToCode(@RequestParam("stkNm") String stkNm) {
    String stkCode = stockDetailSVC.getStkCodeByStkNm(stkNm);

    if (stkCode == null) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }

    Map<String, String> response = new HashMap<>();
    response.put("stkCode", stkCode);
    return ApiResponse.of(ApiResponseCode.SUCCESS, response);
  }
}
