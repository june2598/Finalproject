from fastapi import APIRouter
from fastapi.responses import FileResponse
from wordcloud import WordCloud
import os
import uuid
import io
import base64

router = APIRouter()

# 이미지 저장 경로
IMAGE_DIR = "images"
os.makedirs(IMAGE_DIR, exist_ok=True)  # 이미지 디렉토리 생성

@router.get("/wordcloud")
async def generate_wordcloud():
  # 워드 클라우드 생성을 위한 텍스트 데이터
  text = "Python FastAPI Spring Boot Word Cloud Visualization Example"

  # 워드 클라우드 생성
  wordcloud = WordCloud(width=800, height=400, background_color='white').generate(text)

  # 이미지를 메모리에 저장
  img = io.BytesIO()
  wordcloud.to_image().save(img, format='PNG')
  img.seek(0)

  # Base64로 인코딩
  img_base64 = base64.b64encode(img.getvalue()).decode('utf-8')
  return {"image": f"data:image/png;base64,{img_base64}"}

@router.get("/wordcloud2")
async def generate_wordcloud2():
  # 워드 클라우드 생성을 위한 텍스트 데이터
  text = "Python FastAPI Spring Boot Word Cloud Visualization Example"

  # 워드 클라우드 생성
  wordcloud = WordCloud(width=800, height=400, background_color='white').generate(text)

  # 중복 방지를 위한 UUID 생성
  filename = f"{uuid.uuid4()}.png"
  file_path = os.path.join(IMAGE_DIR, filename)

  # 이미지 파일로 저장
  wordcloud.to_file(file_path)

  # 이미지 URL 경로 생성
  image_url = f"http://localhost:8000/images/{filename}"
  return {"image_path": image_url}  # 파일 URL 반환

@router.get("/images/{filename}")
async def get_image(filename: str):
  file_path = os.path.join(IMAGE_DIR, filename)
  return FileResponse(file_path)  # 이미지 파일 서빙