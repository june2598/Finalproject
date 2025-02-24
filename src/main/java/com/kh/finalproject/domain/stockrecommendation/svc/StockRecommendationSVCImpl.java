package com.kh.finalproject.domain.stockrecommendation.svc;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
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
  public List<RecStk> listByTraitSector(HttpServletRequest request, String inputDate) {
    return stockRecommendationDAO.listByTraitSector(request, inputDate);
  }

  @Override
  public List<RecStk> listWithoutTraitSector(HttpServletRequest request, String inputDate) {
    return stockRecommendationDAO.listWithoutTraitSector(request, inputDate);
  }

  @Override
  public String findIntSecNmByIntSecId(HttpServletRequest request) {
    return stockRecommendationDAO.findIntSecNmByIntSecId(request);
  }

  @Override
  public String findIntSecNmByIntSecIdFromDto(MemberTraitsDto memberTraitsDto) {
    return stockRecommendationDAO.findIntSecNmByIntSecIdFromDto(memberTraitsDto);
  }
}
