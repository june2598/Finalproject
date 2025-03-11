from fastapi import APIRouter
from app.services.community_scraper_service import ScraperService
from datetime import datetime, timedelta
from pydantic import BaseModel

router = APIRouter()
scraper_service = ScraperService()

class ScrapeRequest(BaseModel):
    start_date: str = (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d")
    end_date: str = datetime.now().strftime("%Y-%m-%d")

@router.post("/scrape")
def scrape_comments(request: ScrapeRequest = ScrapeRequest()):
    result = scraper_service.run_scraper(request.start_date, request.end_date)
    return {"success": True, **result}