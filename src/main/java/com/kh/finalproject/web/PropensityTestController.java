package com.kh.finalproject.web;

import com.kh.finalproject.domain.PropertyTest.svc.PropensityTestSVC;
import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor

public class PropensityTestController {

  private final PropensityTestSVC propensityTestSVC;
  // URL 경로 상수 정의
  private static final String PROPENSITY_TEST_PREFIX = "/propensity-test/";
  private static final String propensity_test_root = "/propensityTest/";

  @GetMapping
  public String index() {
    return "index";
  }


  @GetMapping(PROPENSITY_TEST_PREFIX + "info")
  public String showTestInfo() {
    return propensity_test_root + "testInfo"; // 성향 검사 소개 페이지
  }


  // 위험단계 검사 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "risks")
  public String showRiskTest(Model model) {
    model.addAttribute("memberTraits",new MemberTraitsDto());
    return propensity_test_root + "riskTest"; // 위험 단계 설정 페이지
  }

  // 위험단계 제출
  @PostMapping(PROPENSITY_TEST_PREFIX + "risks")
  public String selectRisk(MemberTraitsDto memberTraits,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

    log.info("위험단계 설정 : {}", memberTraits.getMemberRisk());

    // 세션에 memberTraits 저장
    session.setAttribute("memberTraits", memberTraits); // 세션에 저장

    redirectAttributes.addFlashAttribute("memberTraits", memberTraits);
    return "redirect:/propensity-test/sectors";
  }

  // 관심 업종 페이지 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "sectors")
  public String showInterestSectors(Model model) {
    int memberRisk = ((MemberTraitsDto) model.asMap().get("memberTraits")).getMemberRisk(); // 위험도 가져오기
    List<TraitRecSec> sectors = propensityTestSVC.listAll(memberRisk); // 업종 리스트 불러오기
    // MARKET_ID로 그룹화
    Map<Integer, List<TraitRecSec>> groupedSectors = sectors.stream()
        .collect(Collectors.groupingBy(TraitRecSec::getMarketId));

    // MARKET_ID와 이름 매핑
    Map<Integer, String> marketNames = new HashMap<>();
    marketNames.put(1, "KOSPI");
    marketNames.put(2, "KOSDAQ");
    marketNames.put(3, "ETF");

    // 각 MARKET_ID 그룹에서 IS_REC이 가장 높은 3개의 업종 선택
    Map<Integer, List<TraitRecSec>> topSectors = new HashMap<>();
    for (Map.Entry<Integer, List<TraitRecSec>> entry : groupedSectors.entrySet()) {
      List<TraitRecSec> topThree = entry.getValue().stream()
          .sorted((s1, s2) -> Integer.compare(s2.getIsRec(), s1.getIsRec())) // IS_REC 기준 내림차순 정렬
          .limit(3) // 상위 3개 선택
          .collect(Collectors.toList());
      topSectors.put(entry.getKey(), topThree);
    }

    model.addAttribute("groupedSectors", groupedSectors); // 모델에 그룹화된 업종 추가
    model.addAttribute("topSectors", topSectors); // 모델에 추천 업종 추가
    model.addAttribute("marketNames", marketNames); // 모델에 MARKET_ID와 이름 매핑 추가

    return propensity_test_root + "interestSectors"; // 관심 업종 설정 페이지
  }


  // 관심 업종 정보 제출
  @PostMapping(PROPENSITY_TEST_PREFIX + "sectors")
  public String selectSectors(@RequestParam(name = "selectedSectors", required = false) List<Integer> selectedSectors,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
    //세션에서 memberTraits 가져오기
    MemberTraitsDto memberTraits = (MemberTraitsDto) session.getAttribute("memberTraits");


    int memberRisk = memberTraits.getMemberRisk();

    if (selectedSectors == null || selectedSectors.isEmpty()){
      //관심 업종을 선택하지 않은 경우
      log.info("관심업종을 선택하지 않았습니다. 모든 종목 내에서 최대 희망수익률을 설정");

      // 모든 종목 내에서 최대 희망 수익률 설정 로직 호출
      Optional<Double> optionalMaxRtn = propensityTestSVC.findMaxRtn(memberRisk);
      Double maxRtn = optionalMaxRtn.orElse(null); // 결과가 없을 경우 null로 설정

      // 결과를 플래시 속성에 추가
      redirectAttributes.addFlashAttribute("maxRtn",maxRtn);
    } else {
      log.info("관심업종 선택됨: {}", selectedSectors);

      // 선택된 업종 내에서 최대 희망 수익률 설정 로직 호출
      String selectedSectorIds = selectedSectors.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(",")); // 리스트를 쉼표로 구분된 문자열로 변환
      Optional<Double> optionalMaxRtn = propensityTestSVC.findMaxRtn(memberRisk, selectedSectorIds);
      Double maxRtn = optionalMaxRtn.orElse(null); // 결과가 없을 경우 null로 설정

      // 결과를 플래시 속성에 추가
      redirectAttributes.addFlashAttribute("maxRtn", maxRtn);
    }
    // 희망 수익률 페이지로
    return "redirect:/propensity-test/min-return";
  }

  // 희망 수익률 설정 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "min-return")
  public String showMinReturn(Model model, HttpSession session) {
    MemberTraitsDto memberTraits = (MemberTraitsDto) session.getAttribute("memberTraits");
    model.addAttribute("memberTraits", memberTraits); // 모델에 memberTraits 추가
    return propensity_test_root + "setMinReturn"; // 최소 수익률 설정 페이지

  }

  @GetMapping(PROPENSITY_TEST_PREFIX + "finish")
  public String showTestFinish() {
    return propensity_test_root + "testFinish";
  }



}
