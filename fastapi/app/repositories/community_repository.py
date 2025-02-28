from app.core.database import get_db_connection


class CommunityRepository:
  def __init__(self):
    self.conn = get_db_connection()
    self.cursor = self.conn.cursor()

  def save_comment(self, stock_code, content, sentiment_label, post_date):
    """커뮤니티 댓글을 DB에 저장하는 함수"""
    query = """
            INSERT INTO COMMUNITY (COM_ID, STK_ID, CONTENT, COM_POS_LABEL, POST_DATE)
            SELECT COMMUNITY_SEQ.NEXTVAL, 
                   (SELECT STK_ID FROM MKT_SEC_STK WHERE STK_CODE = :1),
                   :2, :3, TO_TIMESTAMP(:4, 'YYYY-MM-DD HH24:MI:SS.FF')
            FROM DUAL
        """
    self.cursor.execute(query, (stock_code, content, sentiment_label, post_date))
    self.conn.commit()

  def close(self):
    """DB 연결 반환 (연결 풀로 되돌림)"""
    self.cursor.close()
    self.conn.close()
