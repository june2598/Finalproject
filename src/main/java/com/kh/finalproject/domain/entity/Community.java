package com.kh.finalproject.domain.entity;

import java.time.LocalDateTime;

public class Community {
  private Long comId;            //  COM_ID	NUMBER(15,0)
  private Long stkId;              //  STK_ID	NUMBER(6,0)
  private String contentToken;              //  CONTENT_TOKEN	CLOB
  private int comPosLabel;            //  COM_POS_LABEL	NUMBER(1,0)
  private LocalDateTime postDate;              //  POST_DATE	TIMESTAMP(6)
  private LocalDateTime cdate;              //  CDATE	TIMESTAMP(6)
  private LocalDateTime udate;              //  UDATE	TIMESTAMP(6)
}
