package com.kh.finalproject.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockListController {

  // 종목리스트 페이지로 이동
  @GetMapping("/stockList")
  public String showStockList() {
    return "stockList/stockList"; // stockList.html을 반환

  }
}
