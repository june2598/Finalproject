package com.kh.finalproject.domain.propertytest.svc;

import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;

import java.util.List;
import java.util.Optional;

public interface PropensityTestSVC {

  //성향 업종 목록
  List<TraitRecSec> listAll();

  //성향 업종 목록 (위험도 선별)
  List<TraitRecSec> listAll(int memberRisk);


  //희망 수익률 최대치 조회 (관심 업종 없음)
  Optional<Double> findMaxRtn(int memberRisk);

  //희망 수익률 최대치 조회 (관심 업종 존재)
  Optional<Double> findMaxRtn(int memberRisk, String intSec);

  //저장
  Long save(MemberTraits memberTraits);

  //성향 조회
  Optional<MemberTraits> findById(Long memberSeq);

  //성향 업데이트
  int updateMemberTraits (Long memberSeq, MemberTraits memberTraits);
}
