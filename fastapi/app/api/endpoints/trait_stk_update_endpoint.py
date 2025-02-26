from fastapi import APIRouter
from app.services.trait_stk_service import update_trait_stk_service

router = APIRouter()

@router.post("/update-trait-stk")
async def update_trait_stk():
    """ `TRAIT_STK` 테이블 업데이트 API """
    return update_trait_stk_service()
