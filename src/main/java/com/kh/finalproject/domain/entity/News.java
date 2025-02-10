package com.kh.finalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString

public class News {

  public Long newsId;                  //  NEWS_ID	NUMBER(15,0)
  public String title;                    //  TITLE	VARCHAR2(255 BYTE)
  public Long stkId;                    //  STK_ID	NUMBER(6,0)
  public int newsPosLabel;                  //  NEWS_POS_LABEL	NUMBER(1,0)
  public String mediaName;                    //  MEDIA_NAME	VARCHAR2(50 BYTE)
  public String newsUrl;                    //  NEWS_URL	VARCHAR2(500 BYTE)
  public LocalDateTime publishedDate;                    //  PUBLISHED_DATE	TIMESTAMP(6)
  public LocalDateTime cdate;                    //  CDATE	TIMESTAMP(6)
  public LocalDateTime udate;                    //  UDATE	TIMESTAMP(6)
}
