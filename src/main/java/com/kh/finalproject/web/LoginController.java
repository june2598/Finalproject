package com.kh.finalproject.web;

import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.dao.MemberDAO;
import com.kh.finalproject.web.form.login.LoginForm;
import com.kh.finalproject.web.form.login.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final MemberDAO memberDAO;

  // 로그인 GET요청
  @GetMapping("/login")
  public String loginForm(Model model){
    model.addAttribute("loginForm",new LoginForm());
    return "/login/loginForm";
  }

  // 로그인 POST요청
  @PostMapping("/login")
  public String login(@Valid LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request){
    log.info("loginForm={}",loginForm);

    //회원 존재 유무
    if(!memberDAO.isExist(loginForm.getMemberId())) {
      bindingResult.rejectValue("memberId","invalidMember");
      return "/login/loginForm";
    }

    //비밀번호 일치 여부
    Optional<Member> optionalMember = memberDAO.findByMemberId(loginForm.getMemberId());
    Member loginMember = optionalMember.get();
    log.info("loginMember={}",loginMember);

    if(!loginForm.getPw().equals(loginMember.getPw())) {
      bindingResult.rejectValue("pw", "invalidMember");
      return "/login/loginForm";
    }

    //로그인 세션 변경

    //세션이 존재하면 해당 세션을 가져오고 없으면 신규 생성

    HttpSession session = request.getSession(true);

    LoginMember loginOkMember = new LoginMember(
        loginMember.getMemberSeq(),
        loginMember.getMemberId(),
        loginMember.getEmail(),
        loginMember.getMemberClsfc()
    );

    session.setAttribute("loginOkMember",loginOkMember);

    log.info("세션에 저장된 회원 정보: {}", session.getAttribute("loginOkMember"));

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

}
