# 현재 정적 파일을 제공하고 있으므로 이 endpoint는 불필요
# wordcloud를 fastapi에서 작성하고 그를 api로 전달하는 과정은 지연시간이 많이 발생하였기 때문에 정적 파일 서빙으로 변경
# 추후에 지연시간을 개선할 수 있으면 다시 endpoint와 routes 구축 필요



# from fastapi import APIRouter
# from fastapi.responses import FileResponse
# import os
# import uuid
# import base64
#
# router = APIRouter()
#
# # 현재 파일(`wordcloud_trend.py`)의 위치를 기준으로 `app/images/wordcloud/` 절대 경로 설정
# BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))  # `fastapi/app/`
# IMAGE_DIR = os.path.join(BASE_DIR, "images", "wordcloud")
# cached_wordcloud_path = os.path.join(IMAGE_DIR, "wordcloud.png")
#
#
# @router.get("/wordcloud")
# async def get_wordcloud():
#     """ ✅ FastAPI에서 미리 저장된 워드 클라우드 이미지를 반환 """
#     if not os.path.exists(cached_wordcloud_path):
#         print(f"파일 없음: {cached_wordcloud_path}")
#         return {"error": "워드 클라우드 이미지가 아직 생성되지 않았습니다."}
#
#     print(f"파일 찾음: {cached_wordcloud_path}")
#     return FileResponse(cached_wordcloud_path, media_type="image/png")
#
