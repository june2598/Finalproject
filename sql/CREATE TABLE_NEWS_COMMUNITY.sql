DROP TABLE COMMUNITY CASCADE CONSTRAINTS;
DROP TABLE NEWS CASCADE CONSTRAINTS;

DROP SEQUENCE COMMUNITY_SEQ;
DROP SEQUENCE NEWS_SEQ;



CREATE TABLE COMMUNITY (
    COM_ID          NUMBER(15) NOT NULL,                    -- Ŀ�´�Ƽ ID ������
    STK_ID          NUMBER(6) NOT NULL,                     -- ���� ID(STOCKS ����)
    CONTENT_TOKEN   CLOB,                                   -- ����_��ū
    COM_POS_LABEL   NUMBER(1) NOT NULL,                     -- ����/���� ��
    POST_DATE       TIMESTAMP NOT NULL,                     -- �Խó�¥
    CDATE           TIMESTAMP DEFAULT SYSDATE NOT NULL,     -- ������ ���� ��¥
    UDATE           TIMESTAMP,                              -- ������ ���� ��¥
    CONSTRAINT PK_COMMUNITY PRIMARY KEY (COM_ID),
    CONSTRAINT FK_COM_STK_ID FOREIGN KEY (STK_ID) REFERENCES STOCKS (STK_ID)
);

CREATE TABLE NEWS (
    NEWS_ID         NUMBER(15) NOT NULL,                    -- ���� ID ������
    TITLE           VARCHAR2(255) NOT NULL,                 -- ���� ��� ����
    STK_ID          NUMBER(6) NOT NULL,                     -- ���� ID(STOCKS ����)
    NEWS_POS_LABEL  NUMBER(1) NOT NULL,                     -- ����/���� ��
    MEDIA_NAME      VARCHAR2(50) NOT NULL,                  -- �Խ� ��л�
    NEWS_URL        VARCHAR2(500) NOT NULL,                 -- ���� ��� ��ũ
    PUBLISHED_DATE  TIMESTAMP NOT NULL,                     -- ���� �Խ� ��¥
    CDATE           TIMESTAMP DEFAULT SYSDATE NOT NULL,     -- ������ ���� ��¥
    UDATE           TIMESTAMP,                              -- ������ ���� ��¥
    CONSTRAINT PK_NEWS PRIMARY KEY (NEWS_ID),    
    CONSTRAINT FK_NEWS_STK_ID FOREIGN KEY (STK_ID) REFERENCES STOCKS (STK_ID) 
);


CREATE SEQUENCE COMMUNITY_SEQ START WITH 1 INCREMENT BY 1;        
CREATE SEQUENCE NEWS_SEQ START WITH 1 INCREMENT BY 1;          

COMMIT;