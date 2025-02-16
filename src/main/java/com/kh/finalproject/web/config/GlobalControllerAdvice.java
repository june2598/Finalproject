package com.kh.finalproject.web.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GlobalControllerAdvice
 *
 * - 모든 컨트롤러에서 공통적으로 사용할 수 있는 기능을 제공하는 클래스
 * - 전역적으로 예외 처리 및 공통 속성(ModelAttribute) 추가 등의 역할
 */

@ControllerAdvice
public class GlobalControllerAdvice {

  /**
   * 세션에 저장된 "error" 메시지를 모델에 추가하는 메서드
   *
   * - 로그인 체크 인터셉터에서 저장한 "error" 메시지를 가져와서 화면(View)에서 사용할 수 있도록 합.
   * - 메시지를 한 번만 표시하기 위해 session에서 제거.
   *
   * @param session 현재 요청의 HttpSession
   * @param model   View에 데이터를 전달하는 Model 객체
   */


  @ModelAttribute
  public void addErrorToModel(HttpSession session, Model model) {

    // 로그인 유무 관련 에러 메시지 처리
    String errorMessage = (String) session.getAttribute("error");
    if (errorMessage != null) {
      model.addAttribute("error", errorMessage);
      session.removeAttribute("error"); // 한 번만 표시되도록 제거
    }

    // 투자 성향 유무 관련 에러 메시지 처리
    String noTraitsError = (String) session.getAttribute("noTraitsError");
    if (noTraitsError != null) {
      model.addAttribute("noTraitsError", noTraitsError);
      session.removeAttribute("noTraitsError"); // 한 번만 표시되도록 제거
    }
  }


}
