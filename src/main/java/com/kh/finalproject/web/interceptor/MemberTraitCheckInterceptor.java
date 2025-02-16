package com.kh.finalproject.web.interceptor;

import com.kh.finalproject.domain.entity.MemberTraits;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberTraitCheckInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    HttpSession session = request.getSession();
    // 세션에서 성향정보 조회
    MemberTraits memberTraits = (MemberTraits) session.getAttribute("memberTraits");

    if(memberTraits == null) {
      // Flash 메시지처럼 사용할 세션 속성 추가
      session.setAttribute("noTraitsError", "투자 성향 검사를 진행한 기록이 없습니다.");
      response.sendRedirect("/propensity-test/info");
      return false;
    }
    return true;

  }
}
