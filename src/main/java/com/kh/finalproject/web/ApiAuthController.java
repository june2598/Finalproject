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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class ApiAuthController {

  private final EmailAuthSVC emailAuthSVC;
  private final MemberSVC memberSVC;
  private final BCryptPasswordEncoder passwordEncoder;

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

    log.info("🔹 ApiAuthController BCryptPasswordEncoder: {}", System.identityHashCode(passwordEncoder));

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

    // 이메일 유효성 검사

    if (!memberSVC.isValidTel(joinForm.getTel())) {
      response.put("success", false);
      response.put("message", "유효한 전화번호를 입력하세요.");
      return ResponseEntity.badRequest().body(response);
    }

    // 비밀번호 확인
    if (!joinForm.getPw().equals(joinForm.getPwConfirm())) {
      response.put("success", false);
      response.put("message", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return ResponseEntity.badRequest().body(response); // 400 Bad Request 응답
    }

    // 이메일 중복 검사
    Optional<String> existingMemberId = memberSVC.findMemberIdByEmail(joinForm.getEmail());
    if (existingMemberId.isPresent()) {
      response.put("success", false);
      response.put("message", "이미 사용 중인 이메일입니다.");
      return ResponseEntity.badRequest().body(response);
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

  // 인증 이메일 전송 요청 (회원 정보 수정)
  @PostMapping("/send-verification-email")
  public ResponseEntity<Map<String, String>> sendAuthenticationEmailFromMemberInfo(
      @RequestBody EmailAuthDto emailAuthDto) {

    emailAuthSVC.sendVerificationEmail(emailAuthDto.getEmail());
    Map<String, String> response = new HashMap<>();
    response.put("message", "이메일이 전송되었습니다.");
    return ResponseEntity.ok(response);

  }


  // 인증 이메일 전송 요청 + 유효성체크
  @PostMapping("/send-verification-email/join")
  public ResponseEntity<Map<String, String>> sendAuthenticationEmail(@RequestBody EmailAuthDto emailAuthDto) {

    Map<String, String> response = new HashMap<>();
    String email = emailAuthDto.getEmail();

    // 서버영역(백엔드) 이메일 유효성 검사 (null 체크 + 정규식 검사)
    if (email == null || email.isBlank() || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
      response.put("success", "false");
      response.put("message", "올바른 이메일 형식을 입력해 주세요.");
      return ResponseEntity.badRequest().body(response);
    }

    // 이메일 중복 검사
    Optional<String> existingMemberId = memberSVC.findMemberIdByEmail(email);
    if (existingMemberId.isPresent()) {
      response.put("success", "false");
      response.put("message", "이미 사용 중인 이메일입니다.");
      return ResponseEntity.badRequest().body(response);
    }

    try {
      // 이메일 인증 코드 전송
      emailAuthSVC.sendVerificationEmail(email);
      response.put("success", "true");
      response.put("message", "이메일 인증 코드가 전송되었습니다.");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("이메일 전송 중 오류 발생: {}", e.getMessage());
      response.put("success", "false");
      response.put("message", "이메일 전송 중 오류가 발생했습니다.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
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
    }

    return ResponseEntity.ok(response);
  }


}
