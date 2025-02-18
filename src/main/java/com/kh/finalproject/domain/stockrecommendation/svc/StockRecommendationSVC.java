package com.kh.finalproject.domain.stockrecommendation.svc;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface StockRecommendationSVC {
  // 추천 종목 목록(관심 업종이 있을때)
  List<RecStk> listByTraitSector(HttpServletRequest request);

  // 추천 종목 목록(관심 업종이 없을때)
  List<RecStk> listWithoutTraitSector(HttpServletRequest request);

  // 성향에 저장된 업종 ID로 업종명 찾기
  String findIntSecNmByIntSecId(HttpServletRequest request);

  // 업종 ID로 업종명 찾기(DTO)
  String findIntSecNmByIntSecIdFromDto(MemberTraitsDto memberTraitsDto);


}
