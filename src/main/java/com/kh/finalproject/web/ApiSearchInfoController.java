package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.SearchMemberDto;
import com.kh.finalproject.domain.emailauth.svc.EmailAuthSVC;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/search-info")

public class ApiSearchInfoController {

  @Autowired
  private EmailAuthSVC emailAuthSVC;

  @Autowired
  private MemberSVC memberSVC;

  @PostMapping ("/search-id")
  public Map<String, String> searchMemberId(@RequestBody SearchMemberDto searchMemberDto, Model model) {

    Map<String, String> response = new HashMap<>();
    String email = searchMemberDto.getEmail();

    // 이메일을 입력 안하고 요청했을때 처리
    if (email == null || email.isEmpty()) {
      response.put("message","이메일을 입력해 주세요.");
      return response;
    }

    try {
      Optional<String> memberIdOpt = memberSVC.findMemberIdByEmail(email);
      if (memberIdOpt.isPresent()) {
        String memberId = memberIdOpt.get();
        response.put("message", "고객님의 아이디는 " + memberId + "입니다.");
      } else {
        response.put("message", "해당 이메일로 가입된 아이디가 없습니다.");
      }
    } catch (Exception e) {
      log.error("예상치 못한 에러발생",e);
      response.put("message", "예상치 못한 오류가 발생했습니다.");
    }
    return response;
  }

  @PostMapping("/search-pw")
  public ResponseEntity<Map<String, String>> sendFindPwToEmail(@RequestBody SearchMemberDto searchMemberDto) {
    log.info("searchMemberDto={}", searchMemberDto);
    String email = searchMemberDto.getEmail();;
    String memberId = searchMemberDto.getMemberId();
    memberSVC.sendFindPwToEmail(email,memberId);
    Map<String, String> response = new HashMap<>();
    response.put("message", "가입하신 이메일로 비밀번호가 전송되었습니다.");
    return ResponseEntity.ok(response);
  }
}
