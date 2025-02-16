package com.kh.finalproject.web;

import com.kh.finalproject.domain.dto.MemberInfoDto;
import com.kh.finalproject.domain.entity.Member;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class ApiMemberController {

  private final MemberSVC memberSVC;

  @PostMapping("/update-info")

  public ResponseEntity<Map<String,Object>> updateMemberInfo(@RequestBody MemberInfoDto memberInfoDto) {
    Map<String, Object> response = new HashMap<>();

    Optional<Member> memberOpt = memberSVC.findByMemberId(memberInfoDto.getMemberId());

    if (memberOpt.isEmpty()) {
      response.put("success", false);
      response.put("message", "존재하지 않는 회원입니다.");
      return ResponseEntity.badRequest().body(response);
    }

    Member member = memberOpt.get();
    member.setPw(memberInfoDto.getPw());
    member.setTel(memberInfoDto.getTel());
    member.setEmail(member.getEmail());

    memberSVC.updateById(member.getMemberSeq(), member);

    response.put("success", true);
    response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
    return ResponseEntity.ok(response);

  }

}
