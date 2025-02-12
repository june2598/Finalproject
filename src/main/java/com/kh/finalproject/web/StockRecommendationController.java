package com.kh.finalproject.web;

import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.stockrecommendation.svc.StockRecommendationSVC;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor

public class StockRecommendationController {

  private final StockRecommendationSVC stockRecommendationSVC;

  @GetMapping("/recstk")
  public String showRecStkPage() {
    return "/stockRecommendation/stockRecommendation";
  }

  @PostMapping("recstk")
  public String nextPage() {
    return "redirect:/recstk/list";
  }

  // 기간 제출
  @GetMapping("/recstk/list")
  public String recommendList(Model model, HttpServletRequest request, HttpSession session) {
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    if (memberTraits == null) {
      model.addAttribute("error", "Member traits not found in session.");
      return "error"; // 에러 페이지로 리턴
    }

    List<RecStk> recommendedStocks;

    // 관심 업종이 있는지 체크
    if (memberTraits.getIntSec() != null && !memberTraits.getIntSec().isEmpty()) {
      recommendedStocks = stockRecommendationSVC.listByTraitSector(request);
    } else {
      recommendedStocks = stockRecommendationSVC.listWithoutTraitSector(request);
    }

    // 모델에 추천 종목 추가
    model.addAttribute("recommendedStocks", recommendedStocks);
    return "/stockRecommendation/recommendList";

  }





}
