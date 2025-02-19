package com.kh.finalproject.web;

import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.web.form.login.LoginForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor

public class IndexController {

  @GetMapping
  public String index() {
    return "index";
  }

  // 로그인
  @GetMapping("/login")
  public String loginForm(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "/login/loginForm";
  }

  // 회원가입
  @GetMapping("/register")
  public String showJoinForm() {
    return "/member/joinForm";
  }

  // 회원 성향 검사
  @GetMapping("/propensity-test/" + "info")
  public String showTestInfo(HttpSession session, RedirectAttributes redirectAttributes) {

    // 세션에서 성향 정보 가져오기
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    if (memberTraits != null) {
      redirectAttributes.addFlashAttribute("isExistTraitError", "이미 성향검사를 진행하셨습니다. 조회나 수정을 이용해주세요.");
      return "redirect:/propensity-test/my-page"; // 성향 조회 페이지로 리다이렉트
    }
    return "/propensityTest/testInfo"; // 성향 검사 소개 페이지
  }

  // 종목리스트 페이지로 이동
  @GetMapping("/stockList")
  public String showStockList() {
    return "stockList/stockList"; // stockList.html을 반환
  }

  // 트렌드 페이지로 이동
  @GetMapping("/trend")
  public String showTrendPage() {
    return "trend/trend"; // trend.html을 반환

  }

  // 회원 정보 보기 페이지로 이동
  @GetMapping("/pw-auth")
  public String showMemberInfo() {
    return "member/memberInfoIndex";
  }

  // 종목 상세 지표 페이지로 이동

  @GetMapping("/stockList/stocks")
  public String showStockDetail() {
    return "stockList/stockDetail";
  }
}
