package com.kh.finalproject.domain.PropertyTest.dao;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
class PropensityTestDAOImplTest {
  @Autowired
  PropensityTestDAO propensityTestDAO;

  @Test
  void save() {
  }

  @Test
  @DisplayName("성향 검사 업종 목록")
  void listAll() {
//    List<TraitRecSec> list =
    List<TraitRecSec> list = propensityTestDAO.listAll();
    for (TraitRecSec traitRecSec : list) {
      log.info("traitRecSec={}", traitRecSec);    // 총 11업종 나오면 정상
    }


  }

  @Test
  @DisplayName("성향 검사 업종 목록 : 위험도 필터링")
  void riskListAll() {
    int memberRisk = 2; // 위험도 2이하 업종 필터링
    List<TraitRecSec> list = propensityTestDAO.listAll(memberRisk);
    for (TraitRecSec traitRecSec : list) {
      log.info("traitRecSec={}", traitRecSec); // 5업종 나오면됨, 20,31,59,85,117
    }
  }

  @Test
  @DisplayName("고객 성향 조회")
  void findById() {
    Long memberSeq = 1L;
    Optional<MemberTraitsDto> memberTraits = propensityTestDAO.findById(memberSeq);
    MemberTraitsDto findedMemberTraitsDto = memberTraits.orElseThrow();
    log.info("findedMemberTraits={}", findedMemberTraitsDto);
    Assertions.assertThat(findedMemberTraitsDto.getMemberId()).isEqualTo("test1234");
    Assertions.assertThat(findedMemberTraitsDto.getMemberRisk()).isEqualTo(2L);


  }
}