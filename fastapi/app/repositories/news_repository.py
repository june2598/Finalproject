from app.core.database import get_db_connection

def insert_news(news_data):
    """뉴스 데이터를 DB에 저장"""
    conn = get_db_connection()
    cursor = conn.cursor()

    try:
        # STK_CODE로 STK_ID 조회
        cursor.execute("SELECT STK_ID FROM STOCKS WHERE STK_CODE = :code", {"code": news_data["code"]})
        stk_id_result = cursor.fetchone()

        if not stk_id_result:
            print(f"STK_CODE {news_data['code']}에 해당하는 STK_ID가 없습니다.")
            return

        stk_id = stk_id_result[0]

        # NEWS 테이블에 데이터 삽입
        cursor.execute("""
                INSERT INTO NEWS (
                    NEWS_ID, TITLE, STK_ID, NEWS_CONTENT, NEWS_POS_LABEL, NEWS_TOKEN, MEDIA_NAME, PUBLISHED_DATE, NEWS_URL
                ) VALUES (
                    NEWS_SEQ.NEXTVAL,
                    :title,
                    :stk_id,
                    :content,
                    :news_pos_label,
                    :news_token,
                    :source,
                    TO_TIMESTAMP(:publish_date, 'YYYY-MM-DD HH24:MI:SS'),
                    :link
                )
            """, {
            "title": news_data["title"],
            "stk_id": stk_id,
            "content": news_data["content"],
            "news_pos_label": news_data["news_pos_label"],
            "news_token": news_data["news_token"],
            "source": news_data["source"],
            "publish_date": news_data["date"].strftime("%Y-%m-%d %H:%M:%S"),  # datetime을 문자열로 변환
            "link": news_data["link"]
        })

        conn.commit()
    except Exception as e:
        print(f"DB 저장 중 오류 발생: {e}")  # 오류 메시지 출력
        conn.rollback()
    finally:
        cursor.close()
        conn.close()