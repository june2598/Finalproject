package com.kh.finalproject.web;

import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import com.kh.finalproject.domain.propertytest.svc.PropensityTestSVC;
import com.kh.finalproject.web.form.login.LoginForm;
import com.kh.finalproject.web.form.login.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final MemberSVC memberSVC;
  private final PropensityTestSVC propensityTestSVC;
  private final BCryptPasswordEncoder passwordEncoder;


  // 로그인 POST요청
  @PostMapping("/login")
  public String login(@Valid LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request,
                      RedirectAttributes redirectAttributes) {
    log.info("로그인 요청: {}", loginForm);
    log.info("🔹 LoginController BCryptPasswordEncoder: {}", System.identityHashCode(passwordEncoder));

    // 비밀번호 일치 여부
    Optional<Member> optionalMember = memberSVC.findByMemberId(loginForm.getMemberId());
    if (optionalMember.isEmpty()) {
      bindingResult.rejectValue("memberId", "invalidMember");
      log.warn("회원 정보를 찾을 수 없습니다: {}", loginForm.getMemberId());
      redirectAttributes.addFlashAttribute("error","존재하지 않는 ID 입니다.");
      return "redirect:/login";
    }

    Member loginMember = optionalMember.get();
    log.info("로그인 시도한 회원 정보: {}", loginMember);
    log.info("입력된 비밀번호: {}", loginForm.getPw());
    log.info("저장된 해싱된 비밀번호: {}", loginMember.getPw());

    if (!passwordEncoder.matches(loginForm.getPw(), loginMember.getPw())) {
      log.warn("❌ 비밀번호 검증 실패!");
      log.warn("입력한 비밀번호: '{}'", loginForm.getPw());
      log.warn("DB에 저장된 해싱된 비밀번호: '{}'", loginMember.getPw());

      boolean testMatch = passwordEncoder.matches("Rla81680!", loginMember.getPw());
      log.warn("🔹 테스트 비밀번호 비교 (Rla81680! vs DB): {}", testMatch);

      bindingResult.rejectValue("pw", "invalidMember");
      redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
      return "redirect:/login";
    }


    // 로그인 세션 변경
    HttpSession session = request.getSession(true);
    LoginMember loginOkMember = new LoginMember(
        loginMember.getMemberSeq(),
        loginMember.getMemberId(),
        loginMember.getEmail(),
        loginMember.getMemberClsfc()
    );

    session.setAttribute("loginOkMember", loginOkMember);
    log.info("세션에 저장된 회원 정보: {}", session.getAttribute("loginOkMember"));

    // 성향 정보 저장
    Long memberSeq = loginMember.getMemberSeq();
    storeMemberTraitsInSession(request, memberSeq);

    return "redirect:/";
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request) {
    //세션정보 가져오기
    //false : 세션이 있으면 가져오고 없으면 안가져옴(신규생성안함)
    HttpSession session = request.getSession(false);
    //세션제거
    session.invalidate();
    return "redirect:/";
  }

  @GetMapping("/search-info")
  public String showSearchMemberInfo() {
    return "member/searchMember";
  }



  // 성향 정보를 세션에 저장하는 메서드
  public void storeMemberTraitsInSession(HttpServletRequest request, Long memberSeq) {
    Optional<Member> memberOpt = memberSVC.findByMemberSeq(memberSeq);
    HttpSession session = request.getSession();

    if (memberOpt.isPresent()) {
      Member member = memberOpt.get();

      // 성향 정보를 조회
      Optional<MemberTraits> memberTraitsOpt = propensityTestSVC.findById(member.getMemberSeq());

      if (memberTraitsOpt.isPresent()) {
        MemberTraits memberTraits = memberTraitsOpt.get();
        session.setAttribute("memberTraits", memberTraits);
      }
    }
  }



}
