from fastapi import APIRouter
from app.api.endpoints.wordcloud_trend import router as wordcloud_router

router = APIRouter()

# 엔드포인트 등록
router.include_router(wordcloud_router, prefix="/api", tags=["WordCloud"])
