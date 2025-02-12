package com.kh.finalproject.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.ClassUtils;

import java.util.Collection;
import java.util.Map;

public class ApiResponse<T> {
    @JsonProperty("header")
    private Header header;    //응답헤더
    @JsonProperty("body")
    private T body;          //응답바디
    @JsonProperty("reqCnt")
    private int reqCnt;       //
    @JsonProperty("totalCnt")
    private int totalCnt;    //총건수

    private ApiResponse(Header header, T body, int totalCnt) {
      this.header = header;
      this.body = body;
      this.totalCnt = totalCnt;
    }

    private ApiResponse(Header header, T body, int reqCnt, int totalCnt) {
      this.header = header;
      this.body = body;
      this.reqCnt = reqCnt;
      this.totalCnt = totalCnt;
    }

    // 1. 기본 헤더 (details가 없는 경우)
    @Getter
    @ToString
    private static class Header {
      private String rtcd;      //응답코드
      private String rtmsg;     //응답메시지

      Header(String rtcd, String rtmsg) {
        this.rtcd = rtcd;
        this.rtmsg = rtmsg;
      }
    }

    // 2. 상세 정보가 포함된 헤더 (이너클래스), 요청클라이언트의 상세 오류 메세지
    @Getter
    @ToString
    private static class DetailHeader extends Header {
      private Map<String, String> details; //응답오류 상세

      DetailHeader(String rtcd, String rtmsg, Map<String, String> details) {
        super(rtcd, rtmsg);
        this.details = details;
      }
    }

    // 3. 기본 응답 생성 (details 없는 경우)
    public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T body) {
      return new ApiResponse<>(
          new Header(responseCode.getRtcd(), responseCode.getRtmsg()),
          body,
          calculateRecCount(body)
      );
    }

    // 3. 기본 응답 생성 (details 없는 경우, totalCnt)
    public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T body, int totalCnt) {
      return new ApiResponse<>(
          new Header(responseCode.getRtcd(), responseCode.getRtmsg()),
          body,
          calculateRecCount(body),
          totalCnt
      );
    }

    // 4. 상세 정보를 포함한 응답 생성
    public static <T> ApiResponse<T> withDetails(
        ApiResponseCode responseCode,
        Map<String, String> details,
        T body) {
      return new ApiResponse<>(
          new DetailHeader(responseCode.getRtcd(), responseCode.getRtmsg(), details),
          body,
          calculateRecCount(body)
      );
    }


    // 4. 상세 정보를 포함한 응답 생성 (totalCnt)
    public static <T> ApiResponse<T> withDetails(
        ApiResponseCode responseCode,
        Map<String, String> details,
        T body,
        int totalCnt) {
      return new ApiResponse<>(
          new DetailHeader(responseCode.getRtcd(), responseCode.getRtmsg(), details),
          body,
          calculateRecCount(body),
          totalCnt
      );
    }

    // 5. totalCnt 계산 로직
    private static <T> int calculateRecCount(T body) {
      if (body == null) return 0;

      if (ClassUtils.isAssignable(Collection.class, body.getClass())) {
        return ((Collection<?>) body).size();
      } else if (ClassUtils.isAssignable(Map.class, body.getClass())) {
        return ((Map<?, ?>) body).size();
      }
      return 1;
    }
}
