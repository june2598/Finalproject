package com.kh.finalproject.web.interceptor;

import com.kh.finalproject.web.form.login.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
    HttpSession session = request.getSession();
    LoginMember loginOkMember = (LoginMember) session.getAttribute("loginOkMember");

    if (loginOkMember == null) {
      session.setAttribute("error", "로그인을 진행해야 합니다.");
      response.sendRedirect("/login");
      return false;
    }
    return true;
  }

}
