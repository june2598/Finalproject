package com.kh.finalproject.domain.stocklist.svc;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;
import com.kh.finalproject.domain.stocklist.dao.StockDetailDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
