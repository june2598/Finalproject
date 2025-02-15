package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.MemberInfoDto;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.dao.MemberDAO;
import com.kh.finalproject.web.form.login.LoginMember;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor

public class MemberController {

  private final MemberDAO memberDAO;

  // 회원정보 조회 전 비밀번호 인증
  @PostMapping("/pw-auth")
  public String pwAuth(HttpSession session, @RequestParam("pw") String inputPw, Model model) {
    // 세션에서 로그인된 회원 정보 가져오기
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");
    // 시퀀스 가져오기
    Long memberSeq = loginOkMember.getMemberSeq();
    // 시퀀스로부터 회원정보
    Optional<Member> memberInfo = memberDAO.findByMemberSeq(memberSeq);
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

    String currentPw = member.getPw();

    // 입력된 비밀번호와 현재 비밀번호 비교
    if (inputPw.equals(currentPw)) {
      // 비밀번호가 일치하는 경우
      model.addAttribute("memberInfo", memberInfoDto);
      return "member/memberInfo"; // 뷰 이름 반환
    } else {
      // 비밀번호가 일치하지 않는 경우
      model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
      return "member/pwAuth"; // 다시 비밀번호 입력 페이지로 이동
    }
  }

}
