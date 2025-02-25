from fastapi import APIRouter
from app.services.rec_stk_update_service import fetch_stock_data

router = APIRouter()

@router.post("/update-rec-stk")
async def update_rec_stk():
    """ 주식 데이터 수집 후 DB 저장 """
    result = fetch_stock_data()
    return result
