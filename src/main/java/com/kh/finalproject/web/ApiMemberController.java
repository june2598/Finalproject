package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.MemberInfoDto;
import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import com.kh.finalproject.domain.stockrecommendation.svc.StockRecommendationSVC;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class ApiMemberController {

  private final MemberSVC memberSVC;
  private final StockRecommendationSVC stockRecommendationSVC;

  // 회원 정보 수정
  @PostMapping("/update")
  public ResponseEntity<Map<String, Object>> updateMemberInfo(@Valid @RequestBody MemberInfoDto memberInfoDto,
                                                              HttpSession session) {
    Map<String, Object> response = new HashMap<>();

    try {
      // 1. 회원 존재 여부 확인
      Optional<Member> memberOpt = memberSVC.findByMemberId(memberInfoDto.getMemberId());
      if (memberOpt.isEmpty()) {
        response.put("success", false);
        response.put("message", "존재하지 않는 회원입니다.");
        return ResponseEntity.badRequest().body(response);
      }

      Member member = memberOpt.get();

      // 2. 유효성 검사 (서버에서 추가 확인)
      if (!isValidPassword(memberInfoDto.getPw())) {
        response.put("success", false);
        response.put("message", "비밀번호는 8~15자리이며, 대소문자/숫자/특수문자를 포함해야 합니다.");
        return ResponseEntity.badRequest().body(response);
      }

      if (!isValidPhone(memberInfoDto.getTel())) {
        response.put("success", false);
        response.put("message", "유효하지 않은 전화번호입니다.");
        return ResponseEntity.badRequest().body(response);
      }

      if (!isValidEmail(memberInfoDto.getEmail())) {
        response.put("success", false);
        response.put("message", "올바른 이메일 형식이 아닙니다.");
        return ResponseEntity.badRequest().body(response);
      }

      // 회원 정보 업데이트
      member.setPw(memberInfoDto.getPw());
      member.setTel(memberInfoDto.getTel());
      member.setEmail(memberInfoDto.getEmail());

      memberSVC.updateById(member.getMemberSeq(), member);


      // 수정후에 세션정보 최신으로 업데이트
      session.setAttribute("loginOkMember", member);

      response.put("success", true);
      response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("회원 정보 수정 중 오류 발생", e);
      response.put("success", false);
      response.put("message", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      return ResponseEntity.status(500).body(response);
    }
  }

  // 위험도 수정
  @PostMapping("/traits/update-risk")
  public ResponseEntity<Map<String, Object>> updateMemberRisk(
      @RequestBody Map<String, Integer> payload, HttpSession session) {
    Map<String, Object> response = new HashMap<>();


    try {

      // 1️⃣ 세션에서 DTO 가져오기 (없으면 엔터티에서 변환)
      MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");

      if (memberTraitsDto == null) {
        MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");
        if (memberTraits == null) {
          log.warn("⚠ 세션에 저장된 성향 데이터가 없습니다!");
          response.put("success", false);
          response.put("message", "로그인 정보가 없습니다.");
          return ResponseEntity.status(401).body(response);
        }
        memberTraitsDto = MemberTraitsDto.fromEntity(memberTraits);
      }

      int newRisk = payload.get("memberRisk"); // 수정된 위험단계

      // 업데이트
      memberTraitsDto.setMemberRisk(newRisk);

      session.setAttribute("memberTraitsDto", memberTraitsDto);

      response.put("success", true);
      response.put("message", "위험도가 성공적으로 업데이트되었습니다.");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("위험도 수정 중 오류 발생", e);
      response.put("success", false);
      response.put("message", "서버 내부 오류가 발생했습니다.");
      return ResponseEntity.status(500).body(response);
    }

  }

  @PostMapping("/traits/update-sectors")
  public ResponseEntity<Map<String, Object>> updateMemberTraitSectors(
      @RequestBody Map<String, List<String>> payload, HttpSession session, HttpServletRequest request) {

    Map<String, Object> response = new HashMap<>();

    try {


      // 1️⃣ 세션에서 DTO 가져오기 (없으면 엔터티에서 변환)
      MemberTraitsDto memberTraitsDto = (MemberTraitsDto) session.getAttribute("memberTraitsDto");

      if (memberTraitsDto == null) {
        MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");
        if (memberTraits == null) {
          log.warn("⚠ 세션에 저장된 성향 데이터가 없습니다!");
          response.put("success", false);
          response.put("message", "로그인 정보가 없습니다.");
          return ResponseEntity.status(401).body(response);
        }
        memberTraitsDto = MemberTraitsDto.fromEntity(memberTraits);
      }


      List<String> newIntSec = payload.get("intSec");
      log.info("변경할 관심 업종 리스트: {}", newIntSec);

      // DTO 업데이트
      memberTraitsDto.setIntSec(newIntSec);

      // 3️새로운 관심 업종 이름 조회 및 업데이트
      String updatedIntSecNm = stockRecommendationSVC.findIntSecNmByIntSecId(request);
      log.info("업데이트된 관심 업종 이름: {}", updatedIntSecNm);

      // 세션 업데이트
      session.setAttribute("memberTraitsDto", memberTraitsDto);
      session.setAttribute("intSecNm", updatedIntSecNm); // 관심 업종 이름도 저장

      response.put("success", true);
      response.put("message", "관심 업종이 성공적으로 업데이트되었습니다.");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("⚠ 관심 업종 수정 중 오류 발생", e);
      response.put("success", false);
      response.put("message", "서버 내부 오류가 발생했습니다.");
      return ResponseEntity.status(500).body(response);
    }

  }

  // 비밀번호 유효성 검사
  private boolean isValidPassword(String pw) {
    return pw != null && pw.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}:<>?]).{8,15}$");
  }

  // 전화번호 유효성 검사
  private boolean isValidPhone(String tel) {
    return tel != null && tel.matches("^\\d{10,11}$");
  }

  // 이메일 유효성 검사
  private boolean isValidEmail(String email) {
    return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
  }
}
