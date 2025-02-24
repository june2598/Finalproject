package com.kh.finalproject.web;

import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.stockrecommendation.svc.StockRecommendationSVC;
import com.kh.finalproject.web.form.login.LoginMember;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor

public class StockRecommendationController {

  private final StockRecommendationSVC stockRecommendationSVC;

  @GetMapping("/recstk")
  public String showRecStkPage(Model model, HttpServletRequest request, HttpSession session,
                               RedirectAttributes redirectAttributes) {
    // 세션에서 성향정보 조회
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    // 성향 정보 모델에 추가
    model.addAttribute("memberTraits", memberTraits);

    // 관심 업종이 있는지 체크
    if (memberTraits.getIntSec() != null && !memberTraits.getIntSec().isEmpty()) {
      String intSecNm = stockRecommendationSVC.findIntSecNmByIntSecId(request);
      model.addAttribute("intSecNm", intSecNm);
    } else {
      model.addAttribute("intSecNm", "없음");
    }
    return "/stockRecommendation/stockRecommendation";
  }

  @PostMapping("recstk")
  public String nextPage(@RequestParam("inputDate") String inputDate,
                         HttpSession session) {

    // 현재 날짜 구하기
    LocalDate today = LocalDate.now();
    LocalDate selectedDate = LocalDate.parse(inputDate); // 입력 받은 날짜를 LocalDate로 변환

    // 날짜 차이 계산
    long period = java.time.temporal.ChronoUnit.DAYS.between(selectedDate, today); // 오늘과 입력된 날짜 간의 차이

    // 입력 받은 기간을 세션에 저장
    session.setAttribute("inputDate", inputDate);
    session.setAttribute("period",period);

    return "redirect:/recstk/list";
  }

  // 기간 제출
  @GetMapping("/recstk/list")
  public String recommendList(Model model, HttpServletRequest request, HttpSession session) {
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    String inputDate = (String) session.getAttribute("inputDate");
    Long period = (Long) session.getAttribute("period");

    // 세션에서 로그인멤버의 id 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    String memberId = loginOkMember.getMemberId(); // 로그인한 회원의 id

    if (memberTraits == null) {
      model.addAttribute("error", "Member traits not found in session.");
      return "error"; // 에러 페이지로 리턴
    }

    List<RecStk> recommendedStocks;

    // 관심 업종이 있는지 체크
    if (memberTraits.getIntSec() != null && !memberTraits.getIntSec().isEmpty()) {
      recommendedStocks = stockRecommendationSVC.listByTraitSector(request, inputDate);
    } else {
      recommendedStocks = stockRecommendationSVC.listWithoutTraitSector(request, inputDate);
    }

    if (recommendedStocks == null || recommendedStocks.isEmpty()) {
      log.warn("추천 종목이 없습니다. SQL을 확인하세요.");
      model.addAttribute("error", "추천할 종목이 없습니다.");
    } else {
      log.info("추천 종목 {}개 반환됨", recommendedStocks.size());
    }

    for (RecStk stock : recommendedStocks) {
      log.info("추천 종목: secNm={}, stkNm={}, recRtn={}, recVol={}, recRisk={}",
          stock.getSecNm(), stock.getStkNm(), stock.getRecRtn(), stock.getRecVol(), stock.getRecRisk());
    }

    // 현재 날짜를 구하고 포맷팅
    LocalDate today = LocalDate.now();
    String formattedToday = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    model.addAttribute("inputDate", inputDate);
    model.addAttribute("period",period);
    model.addAttribute("today", formattedToday);
    model.addAttribute("memberId",memberId);

    // 모델에 추천 종목 추가
    model.addAttribute("recommendedStocks", recommendedStocks);
    return "/stockRecommendation/recommendList";

  }





}
