package com.kh.finalproject.web;


import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.stocklist.svc.StockListSVC;
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
@RequestMapping("/api/stockList")
@RequiredArgsConstructor

public class ApiStockListController {

  private final StockListSVC stockListSVC;

  //종목 리스트 불러오기

  @GetMapping
  public ApiResponse<List<StockListDto>> stockList (
      @RequestParam(value = "marketId", defaultValue = "1") Integer marketId,
      @RequestParam(value = "orderBy", defaultValue = "MARCAP") String orderBy,
      @RequestParam(value = "risk", defaultValue = "3") Integer risk,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset
      ) {

    log.info("Fetching sector trend for marketId: {}, orderBy: {}, risk: {}, offset: {}", marketId, orderBy, risk, offset);

    ApiResponse<List<StockListDto>> res = null;
    List<StockListDto> stockList = stockListSVC.getStockList(marketId, orderBy, risk, offset);

    // 데이터가 없을 경우 페이지를 초기화
    if (stockList.isEmpty()) {
      // 페이지를 초기화하기 위해 기본값으로 설정
      int resetOffset = 0; // 페이지 초기화
      stockList = stockListSVC.getStockList(marketId, orderBy, risk, resetOffset); // 기본값으로 데이터 요청
    }
    // 데이터가 여전히 없으면 예외 발생
    if (stockList.isEmpty()) {
      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, null);
    }

    res = ApiResponse.of(ApiResponseCode.SUCCESS, stockList);
    return res;

  }



}
