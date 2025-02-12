package com.kh.finalproject.web.exception;

import com.kh.finalproject.web.api.ApiResponse;
import com.kh.finalproject.web.api.ApiResponseCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {
  private final ApiResponseCode responseCode;
  private final Map<String, String> details;

  // 기본 생성자 - details 없음
  public BusinessException(ApiResponseCode responseCode) {
    super(responseCode.getRtmsg());
    this.responseCode = responseCode;
    this.details = null;
  }

  // details가 필요한 경우의 생성자
  public BusinessException(ApiResponseCode responseCode, Map<String, String> details) {
    super(responseCode.getRtmsg());
    this.responseCode = responseCode;
    this.details = details;
  }

  // ApiResponse 생성 헬퍼 메소드
  public ApiResponse<Object> toResponse() {
    return details != null
        ? ApiResponse.withDetails(responseCode, details, null)
        : ApiResponse.of(responseCode, null);
  }

}
