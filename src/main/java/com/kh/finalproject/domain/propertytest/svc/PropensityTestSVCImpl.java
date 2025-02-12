package com.kh.finalproject.domain.propertytest.svc;

import com.kh.finalproject.domain.entity.MemberTraits;
import com.kh.finalproject.domain.propertytest.dao.PropensityTestDAO;
import com.kh.finalproject.web.form.propensityTest.TraitRecSec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropensityTestSVCImpl implements PropensityTestSVC{

  private final PropensityTestDAO propensityTestDAO;



  @Override
  public List<TraitRecSec> listAll() {
    return propensityTestDAO.listAll();
  }

  @Override
  public List<TraitRecSec> listAll(int memberRisk) {
    return propensityTestDAO.listAll(memberRisk);
  }

  @Override
  public Long save(MemberTraits memberTraits) {
    return propensityTestDAO.save(memberTraits);
  }

  @Override
  public Optional<MemberTraits> findById(Long memberSeq) {
    return propensityTestDAO.findById(memberSeq);
  }

  @Override
  public Optional<Double> findMaxRtn(int memberRisk) {
    return propensityTestDAO.findMaxRtn(memberRisk);
  }

  @Override
  public Optional<Double> findMaxRtn(int memberRisk, String intSec) {
    return propensityTestDAO.findMaxRtn(memberRisk, intSec);
  }
}
