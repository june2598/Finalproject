from fastapi import APIRouter
from pydantic import BaseModel
from app.services.real_time_stock_update_service import process_market_data

router = APIRouter()

class MarketRequest(BaseModel):
    market: str

@router.post("/update-rt-stk")
async def update_rt_stk(request: MarketRequest):
    """
    JSON 요청을 받아 RT_STK 테이블을 업데이트하는 API 엔드포인트
    """
    market = request.market  # JSON 본문에서 market 값 가져오기

    if market == "KOSPI":
        result = process_market_data('KOSPI', 'Code', 'Close', 'Changes', 'ChagesRatio', 'Volume', 'Amount', 'Marcap', include_market=True)
    elif market == "KOSDAQ":
        result = process_market_data('KOSDAQ', 'Code', 'Close', 'Changes', 'ChagesRatio', 'Volume', 'Amount', 'Marcap', include_market=True)
    elif market == "ETF":
        result = process_market_data('ETF/KR', 'Symbol', 'Price', 'Change', 'ChangeRate', 'Volume', 'Amount', 'MarCap', include_market=False)
    else:
        return {"success": False, "message": "Invalid market type"}

    return result
