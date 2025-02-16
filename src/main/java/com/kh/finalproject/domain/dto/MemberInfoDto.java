package com.kh.finalproject.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class MemberInfoDto {

  private Long memberSeq;

  @NotBlank(message = "아이디는 필수입니다.")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{0,14}$", message = "아이디는 영문, 숫자로 구성된 15글자 이하의 단어여야 하며, 첫 글자는 영어여야 합니다.")
  private String memberId;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하이어야 합니다.")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}:<>?]).*$", message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.")
  private String pw;

  @NotBlank(message = "휴대폰 번호는 필수입니다.")
  @Pattern(regexp = "^\\d{10,11}$", message = "유효한 휴대폰 번호를 입력하세요.")
  private String tel;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "유효한 이메일 주소를 입력하세요.")
  private String email;
}
