from fastapi import APIRouter
from app.services.trait_sec_service import update_trait_sec_service, update_trait_rec_sec_service

router = APIRouter()

@router.post("/update-trait-sec")
async def update_trait_sec():
    """ TRAIT_SEC 테이블 업데이트 API """
    return update_trait_sec_service()

@router.post("/update-trait-rec-sec")
async def update_trait_rec_sec():
    """ TRAIT_REC_SEC 테이블 업데이트 API """
    return update_trait_rec_sec_service()
