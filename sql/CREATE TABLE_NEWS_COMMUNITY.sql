DROP TABLE COMMUNITY CASCADE CONSTRAINTS;
DROP TABLE NEWS CASCADE CONSTRAINTS;

DROP SEQUENCE COMMUNITY_SEQ;
DROP SEQUENCE NEWS_SEQ;



CREATE TABLE COMMUNITY (
    COM_ID          NUMBER(15) NOT NULL,                    -- 커뮤니티 ID 시퀀스
    STK_ID          NUMBER(6) NOT NULL,                     -- 종목 ID(STOCKS 참조)
    CONTENT_TOKEN   CLOB,                                   -- 내용_토큰
    COM_POS_LABEL   NUMBER(1) NOT NULL,                     -- 긍정/부정 라벨
    POST_DATE       TIMESTAMP NOT NULL,                     -- 게시날짜
    CDATE           TIMESTAMP DEFAULT SYSDATE NOT NULL,     -- 데이터 생성 날짜
    UDATE           TIMESTAMP,                              -- 데이터 수정 날짜
    CONSTRAINT PK_COMMUNITY PRIMARY KEY (COM_ID),
    CONSTRAINT FK_COM_STK_ID FOREIGN KEY (STK_ID) REFERENCES STOCKS (STK_ID)
);

CREATE TABLE NEWS (
    NEWS_ID         NUMBER(15) NOT NULL,                    -- 뉴스 ID 시퀀스
    TITLE           VARCHAR2(255) NOT NULL,                 -- 뉴스 기사 제목
    STK_ID          NUMBER(6) NOT NULL,                     -- 종목 ID(STOCKS 참조)
    NEWS_POS_LABEL  NUMBER(1) NOT NULL,                     -- 긍정/부정 라벨
    MEDIA_NAME      VARCHAR2(50) NOT NULL,                  -- 게시 언론사
    NEWS_URL        VARCHAR2(500) NOT NULL,                 -- 뉴스 기사 링크
    PUBLISHED_DATE  TIMESTAMP NOT NULL,                     -- 뉴스 게시 날짜
    CDATE           TIMESTAMP DEFAULT SYSDATE NOT NULL,     -- 데이터 생성 날짜
    UDATE           TIMESTAMP,                              -- 데이터 수정 날짜
    CONSTRAINT PK_NEWS PRIMARY KEY (NEWS_ID),    
    CONSTRAINT FK_NEWS_STK_ID FOREIGN KEY (STK_ID) REFERENCES STOCKS (STK_ID) 
);


CREATE SEQUENCE COMMUNITY_SEQ START WITH 1 INCREMENT BY 1;        
CREATE SEQUENCE NEWS_SEQ START WITH 1 INCREMENT BY 1;          

COMMIT;