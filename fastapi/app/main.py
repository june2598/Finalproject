from fastapi import FastAPI
from app.api.routes import router
import uvicorn
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
import os

app = FastAPI()

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용 (보안 강화 필요)
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
# ✅ 이미지 저장 폴더 설정
IMAGE_DIR = os.path.join("images", "wordcloud")

# /images 경로로 기본 images 폴더 제공
app.mount("/images", StaticFiles(directory="images"), name="images")


app.mount("/images", StaticFiles(directory=IMAGE_DIR), name="images")


# 라우터 등록
app.include_router(router)

if __name__ == '__main__':
    uvicorn.run(app, host="127.0.0.1", port=8000, log_level="info")

