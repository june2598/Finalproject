from fastapi import FastAPI
from app.api.routes import router
import uvicorn
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from app.api.endpoints import community
from app.core.database import pool
from contextlib import asynccontextmanager
import os

# Lifespan 이벤트 핸들러 정의
@asynccontextmanager
async def lifespan(app: FastAPI):
    print("FastAPI 서버 시작")
    yield  # 여기가 실행되면서 서버가 동작함
    print("FastAPI 서버 종료, DB 연결 풀 닫기")
    pool.close()  # FastAPI 종료 시 DB 연결 풀 닫기

# Lifespan 적용
app = FastAPI(lifespan=lifespan)

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용 (보안 강화 필요)
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
# 이미지 저장 폴더 설정
IMAGE_DIR = os.path.join("images", "wordcloud")

# /images 경로로 기본 images 폴더 제공 (정적 파일 서빙)
# 종목 상세 지표 차트 경로
app.mount("/images", StaticFiles(directory="images"), name="images")


# 라우터 등록
app.include_router(router)
app.include_router(community.router)



if __name__ == '__main__':
    uvicorn.run(app, host="127.0.0.1", port=8000, log_level="info")

