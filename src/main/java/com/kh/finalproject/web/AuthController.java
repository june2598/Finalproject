package com.kh.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

  @GetMapping("/register")
  public String showJoinForm() {
    return "/member/joinForm";
  }
}
