package com.kh.finalproject.web.config;

import com.kh.finalproject.web.interceptor.LoginCheckInterceptor;
import com.kh.finalproject.web.interceptor.MemberTraitCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final LoginCheckInterceptor loginCheckInterceptor;
  private final MemberTraitCheckInterceptor memberTraitCheckInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginCheckInterceptor)
        .order(1)
        .addPathPatterns(
            "/pw-auth",     // 회원정보 조회
            "/propensity-test/**"   // 투자 성향 검사 전체
            );

    // 투자 성향 유무 검사 인터셉터
    registry.addInterceptor(memberTraitCheckInterceptor)
        .order(2)  // 실행 순서 2번
        .addPathPatterns(
            "/recstk/**",           // 투자 성향별 종목 추천
            "/propensity-test/my-page");    // 투자 성향 조회
  }


}
