from fastapi import APIRouter
from fastapi.responses import FileResponse
import os
from app.services.wordcloud_service import generate_wordcloud

router = APIRouter()

# 이미지 저장 경로
IMAGE_DIR = "images"
os.makedirs(IMAGE_DIR, exist_ok=True)  # 이미지 디렉토리 생성

@router.get("/wordcloud")
async def generate_wordcloud_trend():
  return generate_wordcloud()


@router.get("/images/{filename}")
async def get_image(filename: str):
  file_path = os.path.join(IMAGE_DIR, filename)
  return FileResponse(file_path)  # 이미지 파일 서빙
