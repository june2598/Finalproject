from app.repositories.trait_sec_repository import update_trait_sec
from app.repositories.trait_rec_sec_repository import update_trait_rec_sec, create_or_replace_trait_rec_sec_view

def update_trait_sec_service():
    """ TRAIT_SEC 업데이트 실행 """
    success, message = update_trait_sec()
    return {"success": success, "message": message}


def update_trait_rec_sec_service():
  """ TRAIT_REC_SEC 업데이트 실행 """
  success, message = create_or_replace_trait_rec_sec_view()  # 먼저 뷰를 생성
  if not success:
    return {"success": False, "message": message}

  success, message = update_trait_rec_sec()  # 그 후 테이블 업데이트
  return {"success": success, "message": message}