# 현재 정적 파일을 제공하고 있으므로 이 라우터는 불필요
# wordcloud를 fastapi에서 작성하고 그를 api로 전달하는 과정은 지연시간이 많이 발생하였기 때문에 정적 파일 서빙으로 변경
# 추후에 지연시간을 개선할 수 있으면 다시 endpoint와 routes 구축 필요



# from fastapi import APIRouter
# from app.api.endpoints.wordcloud_trend import router as wordcloud_router
#
# router = APIRouter()
#
# # 엔드포인트 등록
# router.include_router(wordcloud_router, prefix="/api", tags=["WordCloud"])
