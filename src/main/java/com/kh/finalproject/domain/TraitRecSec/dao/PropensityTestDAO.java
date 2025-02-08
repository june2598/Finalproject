package com.kh.finalproject.domain.TraitRecSec.dao;

import com.kh.finalproject.domain.dto.MemberTraitsDto;
import com.kh.finalproject.domain.vo.TraitRecSec;

import java.util.List;
import java.util.Optional;

public interface PropensityTestDAO {
  //성향 업종 목록
  List<TraitRecSec> listAll();

  //성향 업종 목록 (위험도 선별)
  List<TraitRecSec> listAll(int memberRisk);

  //저장
  Long save(com.kh.finalproject.domain.entity.MemberTraits memberTraits);

  //성향 조회
  Optional<MemberTraitsDto> findById(Long memberSeq);


}
