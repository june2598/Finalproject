package com.kh.finalproject.web;

import com.kh.finalproject.domain.emailauth.svc.EmailAuthSVC;
import com.kh.finalproject.domain.member.svc.MemberSVC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/searchInfo")

public class ApiSearchInfoController {

  @Autowired
  private EmailAuthSVC emailAuthSVC;

  @Autowired
  private MemberSVC memberSVC;


}
