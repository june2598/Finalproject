package com.kh.finalproject.domain.stockrecommendation.svc;

import com.kh.finalproject.domain.stockrecommendation.dao.StockRecommendationDAO;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockRecommendationSVCImpl implements StockRecommendationSVC {

  private final StockRecommendationDAO stockRecommendationDAO;

  @Override
  public List<RecStk> listByTraitSector(HttpServletRequest request) {
    return stockRecommendationDAO.listByTraitSector(request);
  }

  @Override
  public List<RecStk> listWithoutTraitSector(HttpServletRequest request) {
    return stockRecommendationDAO.listWithoutTraitSector(request);
  }

  @Override
  public String findIntSecNmByIntSecId(HttpServletRequest request) {
    return stockRecommendationDAO.findIntSecNmByIntSecId(request);
  }
}
