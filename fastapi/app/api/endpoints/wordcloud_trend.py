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

