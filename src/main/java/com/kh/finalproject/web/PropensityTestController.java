package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.propertytest.svc.PropensityTestSVC;
import com.kh.finalproject.domain.stockrecommendation.svc.StockRecommendationSVC;
import com.kh.finalproject.web.form.login.LoginMember;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor          // final 필드에 대한 생성자 자동 생성

public class PropensityTestController {

  private final PropensityTestSVC propensityTestSVC;
  // URL 경로 상수 정의
  private static final String PROPENSITY_TEST_PREFIX = "/propensity-test/";
  private static final String propensity_test_root = "/propensityTest/";
  private final LoginController loginController;
  private final StockRecommendationSVC stockRecommendationSVC;

  // 위험단계 검사 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "risks")
  public String showRiskTest(HttpSession session, RedirectAttributes redirectAttributes, Model model) {

    // 세션에서 성향 정보 가져오기
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");
    if (memberTraits != null) {
      redirectAttributes.addFlashAttribute("isExistTraitError", "이미 성향검사를 진행하셨습니다. 조회나 수정을 이용해주세요.");
      return "redirect:/"; // 홈 페이지로 리다이렉트
    }

    model.addAttribute("memberTraitsDto",new MemberTraitsDto());
    return propensity_test_root + "riskTest"; // 위험 단계 설정 페이지
  }

  // 위험단계 제출
  @PostMapping(PROPENSITY_TEST_PREFIX + "risks")
  public String selectRisk(MemberTraitsDto memberTraitsDto,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

    log.info("위험단계 설정 : {}", memberTraitsDto.getMemberRisk());

    // 세션에 사용자 특성 정보 저장 (다음 단계에서 사용)
    session.setAttribute("memberTraitsDto", memberTraitsDto); // 세션에 저장

    redirectAttributes.addFlashAttribute("memberTraitsDto", memberTraitsDto);
    return "redirect:/propensity-test/sectors";
  }

  // 관심 업종 페이지 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "sectors")
  public String showInterestSectors(HttpSession session, RedirectAttributes redirectAttributes, Model model) {

    // 세션에서 성향 정보 가져오기
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    if (memberTraits != null) {
      redirectAttributes.addFlashAttribute("isExistTraitError", "이미 성향검사를 진행하셨습니다. 조회나 수정을 이용해주세요.");
      return "redirect:/"; // 홈 페이지로 리다이렉트
    }

    // 플래시 속성에서 위험 단계 정보 추출
    int memberRisk = ((MemberTraitsDto) model.asMap().get("memberTraitsDto")).getMemberRisk(); // 위험도 가져오기

    // 서비스 계층에서 추천 업종 목록 조회
    List<TraitRecSec> sectors = propensityTestSVC.listAll(memberRisk); // 업종 리스트 불러오기

    // 시장 구분(MARKET_ID)별로 업종 그룹화
    Map<Integer, List<TraitRecSec>> groupedSectors = sectors.stream()
        .collect(Collectors.groupingBy(TraitRecSec::getMarketId));

    // 시장 이름 매핑 정보 생성 (1: KOSPI, 2: KOSDAQ, 3: ETF)
    Map<Integer, String> marketNames = new HashMap<>();
    marketNames.put(1, "KOSPI");
    marketNames.put(2, "KOSDAQ");
    marketNames.put(3, "ETF");

    // 시장별 상위 3개 추천 업종 선정
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
  public String selectSectors(@RequestParam(name = "intSec", required = false) List<String> intSec,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
    //세션에서 memberTraits 가져오기
    MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");

    // intSec 값 로그에 출력
    log.info("수신된 intSec 값: {}", intSec);

    int memberRisk = memberTraitsDto.getMemberRisk();

    if (intSec == null || intSec.isEmpty()){
      //관심 업종을 선택하지 않은 경우
      log.info("관심업종을 선택하지 않았습니다. 모든 종목 내에서 최대 희망수익률을 설정");

      // 모든 종목 내에서 최대 희망 수익률 설정 로직 호출
      Optional<Double> optionalMaxRtn = propensityTestSVC.findMaxRtn(memberRisk);
      Double maxRtn = optionalMaxRtn.orElse(null); // 결과가 없을 경우 null로 설정

      // 결과를 플래시 속성에 추가
      redirectAttributes.addFlashAttribute("maxRtn",maxRtn);
    } else {
      log.info("관심업종 선택됨: {}", intSec);

      // 선택한 업종 ID를 intSec에 저장
      memberTraitsDto.setIntSec(intSec);

      // 업종 리스트를 memberRisk에 따라 불러오기
      List<TraitRecSec> availableSectors = propensityTestSVC.listAll(memberRisk);
      log.info("업종 리스트: {}", availableSectors);

      // 선택된 업종 이름을 수집
      List<String> intSecNames = new ArrayList<>();
      for (String sectorId : intSec) {
        // TraitRecSec 객체를 사용하여 업종 이름 찾기
        for (TraitRecSec sector : availableSectors) {
          if (sector.getSecId().toString().equals(sectorId)) {
            intSecNames.add(sector.getSecNm()); // 업종 이름 추가
            break; // 찾으면 루프 종료
          }
        }
      }

      log.info("선택된 업종 이름: {}", intSecNames);
      memberTraitsDto.setIntSecNm(intSecNames);

      // 선택된 업종 내에서 최대 희망 수익률 설정 로직 호출
      String selectedSectorIds = intSec.stream()
          .collect(Collectors.joining(",")); // 리스트를 쉼표로 구분된 문자열로 변환
      Optional<Double> optionalMaxRtn = propensityTestSVC.findMaxRtn(memberTraitsDto.getMemberRisk(), selectedSectorIds);
      Double maxRtn = optionalMaxRtn.orElse(null); // 결과가 없을 경우 null로 설정

      // 결과를 플래시 속성에 추가
      redirectAttributes.addFlashAttribute("maxRtn", maxRtn);
    }
    // 희망 수익률 페이지로
    return "redirect:/propensity-test/min-return";
  }

  // 희망 수익률 설정 요청
  @GetMapping(PROPENSITY_TEST_PREFIX + "min-return")
  public String showMinReturn(RedirectAttributes redirectAttributes, Model model, HttpSession session) {

    // 세션에서 성향 정보 가져오기
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    // 성향 정보가 없는 경우
    if (memberTraits != null) {
      redirectAttributes.addFlashAttribute("isExistTraitError", "이미 성향검사를 진행하셨습니다. 조회나 수정을 이용해주세요.");
      return "redirect:/"; // 홈 페이지로 리다이렉트
    }

    MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");
    model.addAttribute("memberTraitsDto", memberTraitsDto); // 모델에 memberTraits 추가
    return propensity_test_root + "setMinReturn"; // 최소 수익률 설정 페이지

  }

  @PostMapping(PROPENSITY_TEST_PREFIX + "min-return")
  public String selectMinReturn(@RequestParam("expRtn") Double expRtn,
                                HttpSession session,
                                RedirectAttributes redirectAttributes){

    // 세션에서 memberTraits 가져오기
    MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");

    // MemberTraits 객체 생성 및 정보 설정
    // DTO에서 Entity로 변환
    MemberTraits memberTraits = memberTraitsDto.toEntity(); // 변환 메서드 사용

    memberTraits.setIntSec(memberTraitsDto.getIntSec());
    memberTraits.setExpRtn(expRtn);

    // 세션에서 로그인된 회원 정보 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    if (loginOkMember != null) {
      memberTraits.setMemberSeq(loginOkMember.getMemberSeq()); // 실제 회원 ID 설정
    } else {
      // 회원 ID가 없으면 예외 처리 또는 기본값 설정
      log.warn("회원 ID가 세션에 존재하지 않습니다.");
      memberTraits.setMemberSeq(null); // 또는 기본값 설정 (필요 시)
    }
    // 로그 추가: 저장할 데이터 확인
    log.info("저장할 성향 정보: memberSeq={}, memberRisk={}, intSec={}, expRtn={}",
        memberTraits.getMemberSeq(),
        memberTraits.getMemberRisk(),
        memberTraits.getIntSec(), // List<String> 출력
        memberTraits.getExpRtn());

        Long traitId = propensityTestSVC.save(memberTraits);


    // memberTraitsDto 업데이트
    memberTraitsDto.setExpRtn(expRtn);
    session.setAttribute("memberTraitsDto",memberTraitsDto);

    redirectAttributes.addFlashAttribute("minReturn",expRtn);
    redirectAttributes.addFlashAttribute("traitId", traitId);
    return "redirect:/propensity-test/finish";
  }

  @GetMapping(PROPENSITY_TEST_PREFIX + "finish")
  public String showTestFinish(Model model, HttpSession session, HttpServletRequest request) {
    // 세션에서 저장된 memberTraitsDto 가져오기
    MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");

    log.info("memberTraitsDto 내용: {}", memberTraitsDto);
    // 모델에 성향 정보 추가
    model.addAttribute("memberTraitsDto", memberTraitsDto); // DTO를 모델에 추가

    // 세션에서 로그인된 회원정보 가져오기 (재로그인 없이 바로 종목 추천을 받을 수 있게)
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    Long memberSeq = loginOkMember.getMemberSeq(); // 로그인한 회원의 시퀀스

    // 데이터베이스에 저장된 성향정보 현재 세션에 저장
    loginController.storeMemberTraitsInSession(request, memberSeq);


    return propensity_test_root + "testFinish"; // 결과 페이지 경로
  }



  @GetMapping(PROPENSITY_TEST_PREFIX + "my-page")
  public String showMyTraits(Model model, HttpSession session, HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");
    model.addAttribute("memberTraits",memberTraits);

    // 관심 업종이 있는지 체크
    if (memberTraits.getIntSec() != null && !memberTraits.getIntSec().isEmpty()) {
      String intSecNm = stockRecommendationSVC.findIntSecNmByIntSecId(request);
      model.addAttribute("intSecNm", intSecNm);
    } else {
      model.addAttribute("intSecNm", "없음");
    }

    return propensity_test_root + "myPage";

  }

}



