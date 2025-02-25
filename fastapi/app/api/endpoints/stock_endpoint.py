from fastapi import APIRouter
from pydantic import BaseModel
from app.services.stock_service import update_stocks

router = APIRouter()

# 요청 데이터 모델
class UpdateStockRequest(BaseModel):
    market: str
    market_id: int

# 관리자가 API를 호출해서 수동 업데이트 실행

@router.post("/update-stocks")
async def update_stocks_api(request: UpdateStockRequest):
  """
      관리자 API: 종목 데이터를 수동 업데이트하는 엔드포인트
      """
  print(f"📌 요청 데이터: {request}")  # FastAPI 서버에서 데이터 확인용
  try:
    update_stocks(request.market, request.market_id)
    return {"success": True, "message": f"{request.market} 종목 데이터 업데이트 완료!"}
  except Exception as e:
    return {"success": False, "message": str(e)}