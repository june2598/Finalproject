from fastapi import APIRouter
from app.services.news_service import crawl_and_save_news

router = APIRouter()

@router.get("/crawl_news")
def crawl_news():
    return crawl_and_save_news()
