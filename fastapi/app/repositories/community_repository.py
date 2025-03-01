from app.core.database import get_db_connection


class CommunityRepository:

  def save_comment(self, stock_code, content, content_token, sentiment_label, post_date):
    """커뮤니티 댓글을 DB에 저장하는 함수"""

    # post_date 값이 올바른지 디버깅 출력
    # print(f"[DEBUG] post_date 값 확인: {post_date}")

    with get_db_connection() as conn:
      with conn.cursor() as cursor:
        # STK_ID 조회
        cursor.execute("SELECT STK_ID FROM MKT_SEC_STK WHERE STK_CODE = :1", (stock_code,))
        stk_id_result = cursor.fetchone()

        if not stk_id_result:
          print(f"[오류] STK_CODE '{stock_code}'에 해당하는 STK_ID가 없습니다! ")
          return  # STK_ID가 없으면 INSERT 실행하지 않음

        stk_id = stk_id_result[0]  # STK_ID 값 저장
        print(f"[DEBUG] STK_ID 조회 성공: {stk_id}, STOCK_CODE: {stock_code}, 날짜: {post_date}")

        # 데이터 삽입
        query = """
            INSERT INTO COMMUNITY (COM_ID, STK_ID, CONTENT, CONTENT_TOKEN, COM_POS_LABEL, POST_DATE)
            VALUES (COMMUNITY_SEQ.NEXTVAL, :1, :2, :3, :4, TO_TIMESTAMP(:5, 'YYYY-MM-DD HH24:MI:SS.FF'))
        """
        cursor.execute(query, (stk_id, content, content_token, sentiment_label, post_date))
        conn.commit()
        print(f"[SUCCESS] {stock_code} (STK_ID: {stk_id}) 댓글 저장 완료! 🎉")
