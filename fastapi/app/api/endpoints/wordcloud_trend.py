# 현재 정적 파일을 제공하고 있으므로 이 endpoint는 불필요
# wordcloud를 fastapi에서 작성하고 그를 api로 전달하는 과정은 지연시간이 많이 발생하였기 때문에 정적 파일 서빙으로 변경
# 추후에 지연시간을 개선할 수 있으면 다시 endpoint와 routes 구축 필요



from fastapi import APIRouter
from fastapi.responses import FileResponse
from konlpy.tag import Okt
from wordcloud import WordCloud
from sqlalchemy import create_engine
from collections import Counter
import pandas as pd
import os
import uuid
import io
import base64
from app.services.wordcloud_service import get_wordcloud_image

router = APIRouter()

# 이미지 저장 경로
IMAGE_DIR = "images"
os.makedirs(IMAGE_DIR, exist_ok=True)  # 이미지 디렉토리 생성

@router.get("/wordcloud")
async def generate_wordcloud():
  return get_wordcloud_image()


@router.get("/images/{filename}")
async def get_image(filename: str):
  file_path = os.path.join(IMAGE_DIR, filename)
  return FileResponse(file_path)  # 이미지 파일 서빙
