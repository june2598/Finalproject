from app.repositories.trait_stk_repository import insert_trait_stk

def update_trait_stk_service():
    """ TRAIT_STK 데이터 업데이트 실행 """
    success, message = insert_trait_stk()
    return {"success": success, "message": message}
