from fastapi import APIRouter
from app.services.domestic_indices_service import update_domestic_indices

router = APIRouter()

@router.post("/update-domestic-indices")
async def update_indices():
    """국내 시장 지수를 업데이트하는 API"""
    result = update_domestic_indices()
    return result