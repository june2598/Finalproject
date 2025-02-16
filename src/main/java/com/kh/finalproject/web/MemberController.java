package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.MemberInfoDto;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.dao.MemberDAO;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import com.kh.finalproject.web.form.login.LoginMember;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor

public class MemberController {

  private final MemberSVC memberSVC;

  // 회원정보 조회 전 비밀번호 인증
  @PostMapping("/pw-auth")
  public String pwAuth(HttpSession session, @RequestParam("pw") String inputPw, RedirectAttributes redirectAttributes) {
    // 세션에서 로그인된 회원 정보 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    // 시퀀스 가져오기
    Long memberSeq = loginOkMember.getMemberSeq();
    // 시퀀스로부터 회원정보
    Optional<Member> memberInfo = memberSVC.findByMemberSeq(memberSeq);

    if (memberInfo.isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "회원 정보를 찾을 수 없습니다.");
      return "redirect:/login";
    }

    Member member = memberInfo.get();
    String currentPw = member.getPw();

    // 입력된 비밀번호와 현재 비밀번호 비교
    if (inputPw.equals(currentPw)) {
      // 비밀번호가 일치하는 경우
      session.setAttribute("pwAuthenticated", true);     // 비밀번호 인증 상태 저장
      return "redirect:/member-info";
    } else {
      // 비밀번호가 일치하지 않는 경우
      redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
      return "redirect:/pw-auth"; // 다시 비밀번호 입력 페이지로 이동
    }
  }


  // 회원 정보 조회
  @GetMapping("/member-info")
  public String showMemberInfo(HttpSession session, Model model) {

    // 비밀번호 인증 여부 확인
    Boolean isAuthenticated = (Boolean) session.getAttribute("pwAuthenticated");
    if (isAuthenticated == null || !isAuthenticated) {
      return "redirect:/pw-auth";
    }

    // 세션에서 로그인된 회원 정보 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");

    // 시퀀스 가져오기
    Long memberSeq = loginOkMember.getMemberSeq();

    // 시퀀스로부터 회원정보
    Optional<Member> memberInfo = memberSVC.findByMemberSeq(memberSeq);
    if (memberInfo.isEmpty()) {
      model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
      return "redirect:/login";
    }

    Member member = memberInfo.get();

    // Member 객체를 MemberInfoDto로 변환
    MemberInfoDto memberInfoDto = new MemberInfoDto();

    memberInfoDto.setMemberSeq(member.getMemberSeq());
    memberInfoDto.setMemberId(member.getMemberId());
    // 비밀번호 마스킹 처리
    String pwMasked = "*".repeat(member.getPw().length());
    memberInfoDto.setPw(pwMasked);
    memberInfoDto.setTel(member.getTel());
    memberInfoDto.setEmail(member.getEmail());
    model.addAttribute("memberInfoDto", memberInfoDto);

    return "member/memberInfo";

  }

  @PostMapping("/member-info")
  public String updateMemberInfo(@ModelAttribute MemberInfoDto memberInfoDto,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

    // 1. 비밀번호 길이 체크 (8~15자)
    if (memberInfoDto.getPw().length() < 8 || memberInfoDto.getPw().length() > 15) {
      redirectAttributes.addFlashAttribute("error", "비밀번호는 8자 이상 15자 이하이어야 합니다.");
      return "redirect:/member-info";
    }

    // 2. 비밀번호 패턴 체크 (대소문자, 숫자, 특수문자 포함)
    String pwPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}:<>?]).{8,15}$";
    if (!memberInfoDto.getPw().matches(pwPattern)) {
      redirectAttributes.addFlashAttribute("error", "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.");
      return "redirect:/member-info";
    }

    // 세션에서 로그인된 회원 정보 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    Long memberSeq = loginOkMember.getMemberSeq();

    // 회원 정보 업데이트 로직
    Optional<Member> existingMember = memberSVC.findByMemberSeq(memberSeq);
    if (existingMember.isPresent()) {
      Member member = existingMember.get();
      member.setPw(memberInfoDto.getPw());
      member.setTel(memberInfoDto.getTel());
      member.setEmail(memberInfoDto.getEmail());

      memberSVC.updateById(memberSeq, member);
      redirectAttributes.addFlashAttribute("success", "회원 정보가 성공적으로 수정되었습니다.");
    }
    return "redirect:/member-info"; // 수정 후 정보 페이지로 리다이렉트
  }
}
