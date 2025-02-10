package com.kh.finalproject.domain.stockrecommendation.dao;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.web.form.stockRecommendation.RecStk;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class StockRecommendationDAOImplTest {

  @Autowired
  StockRecommendationDAO stockRecommendationDAO;

  @Test
  @DisplayName("종목 추천 - 관심업종 존재")
  void listByTraitSector() {
    // Mock HttpServletRequest와 HttpSession
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    MemberTraitsDto memberTraits = new MemberTraitsDto();

    // 세션에 MEMBER_TRAITS 설정
    memberTraits.setMemberRisk(3);
    memberTraits.setExpRtn(7.1);
    memberTraits.setIntSec(Arrays.asList("31", "59", "118")); // String 리스트로 설정

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("MEMBER_TRAITS")).thenReturn(memberTraits);
    // 메서드 호출
    List<RecStk> list = stockRecommendationDAO.listByTraitSector(request);

    // 결과 로그
    log.info("Number of records returned: {}", list.size());
    if (!list.isEmpty()) {
      log.info("First record: {}", list.get(0));
    }
    Assertions.assertThat(memberTraits.getMemberRisk()).isEqualTo(3);




  }

  @Test
  @DisplayName("종목추천 - 관심업종 없음")
  void listWithoutTraitSector() {
    // Mock HttpServletRequest와 HttpSession
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    MemberTraitsDto memberTraits = new MemberTraitsDto();

    // 세션에 MEMBER_TRAITS 설정
    memberTraits.setMemberRisk(3);
    memberTraits.setExpRtn(7.1);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("MEMBER_TRAITS")).thenReturn(memberTraits);

    // 메서드 호출 (올바른 메서드 호출)
    List<RecStk> list = stockRecommendationDAO.listWithoutTraitSector(request);

    // 결과 로그
    log.info("Number of records returned: {}", list.size());
    for (RecStk recStk : list) {
      log.info("Record: {}", recStk);
    }
    Assertions.assertThat(memberTraits.getMemberRisk()).isEqualTo(3);
  }
}