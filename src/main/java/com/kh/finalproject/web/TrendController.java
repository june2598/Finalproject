package com.kh.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrendController {

  // 트렌드 페이지로 이동
  @GetMapping("/trend")
  public String showTrendPage() {
    return "trend/trend"; // trend.html을 반환

  }
}
