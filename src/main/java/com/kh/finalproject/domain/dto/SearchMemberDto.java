package com.kh.finalproject.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchMemberDto {
  private String email;
  private String memberId;
}
