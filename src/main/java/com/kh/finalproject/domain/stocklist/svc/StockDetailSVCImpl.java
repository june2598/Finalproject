package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;
import com.kh.finalproject.domain.stocklist.dao.StockDetailDAO;
import com.kh.finalproject.web.api.ApiResponseCode;
import com.kh.finalproject.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor

public class StockDetailSVCImpl implements StockDetailSVC{

  private final StockDetailDAO stockDetailDAO;

  @Override
  public List<StockNewsDto> getStockNewsList(Long stkId) {
    return stockDetailDAO.getStockNewsList(stkId);
  }

  @Override
  public List<StockListDto> getStockDetail(String stkCode) {
    return stockDetailDAO.getStockDetail(stkCode);
  }

  @Override
  public Long getStkIdByStkCode(String stkCode) {
    return stockDetailDAO.getStkIdByStkCode(stkCode);
  }

  @Override
  public String getStkNmByStkCode(String stkCode) {
    return stockDetailDAO.getStkNmByStkCode(stkCode);
  }

  @Override
  public String getStkCodeByStkNm(String stkNm) {
    try {
      return stockDetailDAO.getStkCodeByStkNm(stkNm);
    } catch (EmptyResultDataAccessException e) {
      // Map을 사용하여 예외 정보를 전달
      Map<String, String> errorDetail = new HashMap<>();
      errorDetail.put("error", "해당 종목명을 찾을 수 없습니다.");
      errorDetail.put("stkNm", stkNm); // 요청한 종목명을 추가로 제공

      throw new BusinessException(ApiResponseCode.ENTITY_NOT_FOUND, errorDetail);
    }
  }
}
