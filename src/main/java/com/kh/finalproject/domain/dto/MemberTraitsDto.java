package com.kh.finalproject.domain.dto;

import com.kh.finalproject.domain.entity.MemberTraits;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString

// 실제로 출력해야되는 영역
// 성향 검사 중에 임시로 저장되는 영역
// 성향 검사 종료화면에서도 성향검사 결과를 보여줌
public class MemberTraitsDto {
  private Long memberSeq;        // MEMBER_SEQ 회원시퀀스
  private String memberId;       // MEMBER_ID 회원 아이디
  private int memberRisk;        // MEMBER_RISK 위험단계
  private List<String> intSec;   // INT_SEC 관심업종
  private List<String> intSecNm; // 업종 이름
  private double expRtn;         // EXP_RTN 희망 수익률(%)


  // DTO -> entity
  public MemberTraits toEntity() {
    MemberTraits entity = new MemberTraits();
    entity.setMemberRisk(this.memberRisk);
    entity.setIntSec(this.intSec);
    entity.setExpRtn(this.expRtn);
    return entity;
  }

  // entity -> DTO

  public static MemberTraitsDto fromEntity(MemberTraits entity) {
    MemberTraitsDto dto = new MemberTraitsDto();
    dto.setMemberSeq(entity.getMemberSeq());
    dto.setMemberRisk(entity.getMemberRisk());
    dto.setExpRtn(entity.getExpRtn());
    dto.setIntSec(entity.getIntSec());

    return dto;
  }
}