package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.AuthDto;
import com.kh.finalproject.domain.dto.CheckMemberIdDto;
import com.kh.finalproject.domain.dto.EmailAuthDto;
import com.kh.finalproject.domain.emailauth.svc.EmailAuthSVC;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import com.kh.finalproject.web.form.member.JoinForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

  @Autowired
  private EmailAuthSVC emailAuthSVC;

  @Autowired
  private MemberSVC memberSVC;

  // 아이디 중복 검사 요청
  @PostMapping ("/check-member-id")
  public Map<String, String> checkMemberId(@RequestBody CheckMemberIdDto checkMemberIdDto) {

    String memberId = checkMemberIdDto.getMemberId();
    System.out.println("Received memberId: " + memberId); // 로그 추가
    boolean isMember = memberSVC.isMember(memberId);

    Map<String, String> response = new HashMap<>();
    if (isMember) {
      response.put("message", "이미 사용 중인 아이디입니다."); // 중복된 아이디 메시지
    } else {
      response.put("message", "사용 가능한 아이디입니다."); // 사용 가능한 아이디 메시지
    }
    return response;
  }

  // 회원 가입
  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody JoinForm joinForm,
                         BindingResult bindingResult) {

    Map<String, Object> response = new HashMap<>();

    // 유효성 검사 오류 처리
    if (bindingResult.hasErrors()) {
      response.put("success", false);
      response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }

    // 아이디 유효성 체크
    if (!memberSVC.isValidMemberId(joinForm.getMemberId())) {
      response.put("success", false);
      response.put("message", "아이디는 영문, 숫자로 구성된 15글자 이하의 단어여야 하며, 첫 글자는 영어여야 합니다.");
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }

    // 비밀번호 유효성 검사
    if (!memberSVC.isValidPassword(joinForm.getPw())) {
      response.put("success", false);
      response.put("message", "비밀번호는 대소문자, 숫자, 특수문자를 포함한 8글자 이상 15글자 이하 여야 합니다.");
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }

    // 비밀번호 확인
    if (!joinForm.getPw().equals(joinForm.getPwConfirm())) {
      response.put("success", false);
      response.put("message", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }

    Member member = new Member();
    member.setEmail(joinForm.getEmail());
    member.setMemberId(joinForm.getMemberId());
    member.setPw(joinForm.getPw());
    member.setTel(joinForm.getTel());

    try {
      memberSVC.join(member, joinForm.getCode());
      response.put("success", true);
      response.put("message", "회원가입이 완료되었습니다.");
      return ResponseEntity.ok(response); // 200 OK 응답
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }
  }

  // 인증 이메일 전송 요청
  @PostMapping("/send-verification-email")
  public ResponseEntity<Map<String, String>> sendAuthenticationEmail(@RequestBody EmailAuthDto emailAuthDto) {
    emailAuthSVC.sendVerificationEmail(emailAuthDto.getEmail());
    Map<String, String> response = new HashMap<>();
    response.put("message", "이메일이 전송되었습니다.");
    return ResponseEntity.ok(response);
  }

  // 코드 인증
  @PostMapping("/verify-code")
  public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody AuthDto authDto, HttpSession session) {

    log.info("authDto = {}", authDto);
    String email = authDto.getEmail();
    String code = authDto.getCode();
    boolean isValid = emailAuthSVC.validateCode(email,code);

    Map<String, Object> response = new HashMap<>();
    response.put("success", isValid);
    response.put("message", isValid ? "인증 성공" : "인증 실패");

    if (isValid) {
      session.setAttribute("emailVerified",true);
      session.setAttribute("verifiedEmail",email);
//      session.setAttribute("emailVerifiedAt",System.currentTimeMillis()); // 인증 시간 저장(인증 유지 시간에 제한을 두기 위함)
    }

    return ResponseEntity.ok(response);
  }

  // 인증 여부 확인 (클라이언트 상태 유지용도)
//  @GetMapping("/check-email-verified")
//  public ResponseEntity<Map<String, Object>> checkEmailVerified(HttpSession session) {
//    Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
//    Long verifiedAt = (Long) session.getAttribute("emailVerifiedAt");
//    long EXPIRATION_TIME = 5 * 60 * 1000; // 5분 (밀리초 단위)
//
//    Map<String, Object> response = new HashMap<>();
//
//    if (emailVerified != null && emailVerified && verifiedAt != null) {
//      long elapsedTime = System.currentTimeMillis() - verifiedAt;
//
//      if (elapsedTime < EXPIRATION_TIME) {
//        response.put("emailVerified", true);
//        response.put("verifiedEmail", session.getAttribute("verifiedEmail"));
//      } else {
//        session.removeAttribute("emailVerified");
//        session.removeAttribute("verifiedEmail");
//        session.removeAttribute("emailVerifiedAt");
//        response.put("emailVerified", false);
//        response.put("message", "인증 시간이 만료되었습니다. 다시 인증해주세요.");
//      }
//    } else {
//      response.put("emailVerified", false);
//    }
//
//    return ResponseEntity.ok(response);
//  }




}
