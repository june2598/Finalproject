from fastapi import APIRouter
from app.services.community_scraper_service import ScraperService
from datetime import datetime, timedelta

router = APIRouter()
scraper_service = ScraperService()

@router.get("/scrape")
def scrape_comments():
    start_date = (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d")
    end_date = datetime.now().strftime("%Y-%m-%d")
    return scraper_service.run_scraper(start_date, end_date)
