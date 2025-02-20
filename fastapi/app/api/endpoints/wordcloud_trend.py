from fastapi import APIRouter
from fastapi.responses import FileResponse
import os
import uuid
import base64

router = APIRouter()

# 현재 파일(`wordcloud_trend.py`)의 위치를 기준으로 `app/images/wordcloud/` 절대 경로 설정
BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))  # `fastapi/app/`
IMAGE_DIR = os.path.join(BASE_DIR, "images", "wordcloud")
cached_wordcloud_path = os.path.join(IMAGE_DIR, "wordcloud.png")


@router.get("/wordcloud")
async def get_wordcloud():
    """ ✅ FastAPI에서 미리 저장된 워드 클라우드 이미지를 반환 """
    if not os.path.exists(cached_wordcloud_path):
        print(f"파일 없음: {cached_wordcloud_path}")
        return {"error": "워드 클라우드 이미지가 아직 생성되지 않았습니다."}

    print(f"파일 찾음: {cached_wordcloud_path}")
    return FileResponse(cached_wordcloud_path, media_type="image/png")



# @router.get("/wordcloud2")
# async def generate_wordcloud2():
#   word_counts = generate_wordcloud_text()  # ✅ 서비스 레이어 호출
#
#   wordcloud = WordCloud(
#     font_path=r'C:\Windows\Fonts\malgun.ttf',
#     width=1200, height=600,
#     background_color='white',
#     max_words=50,
#     min_word_length=2,
#     random_state=2024
#   ).generate_from_frequencies(word_counts)
#
#   filename = f"{uuid.uuid4()}.png"
#   file_path = os.path.join(IMAGE_DIR, filename)
#   wordcloud.to_file(file_path)
#
#   image_url = f"http://localhost:8000/images/{filename}"
#   return {"image_path": image_url}
#
#
#
# @router.get("/images/wordcloud/{filename}")
# async def get_image(filename: str):
#     file_path = os.path.join(IMAGE_DIR, filename)
#     return FileResponse(file_path)