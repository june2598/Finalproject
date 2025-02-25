from fastapi import APIRouter

from app.api.endpoints.wordcloud_trend import router as wordcloud_router, generate_wordcloud, get_image
from app.api.endpoints.stock_endpoint import router as stock_router, update_stocks_api
from app.api.endpoints.real_time_stock_update_endpoint import router as real_time_stock_router, update_rt_stk
from app.api.endpoints.domestic_indices_endpoint import router as domestic_indices_router, update_indices
from app.api.endpoints.rec_stk_update_endpoint import router as rec_stk_router, update_rec_stk

router = APIRouter()

# 엔드포인트 등록
router.include_router(wordcloud_router, prefix="/api", tags=["WordCloud"])
router.include_router(stock_router, prefix="/api", tags=["Stock"])
router.include_router(real_time_stock_router, prefix="/api", tags=["RtStk"])
router.include_router(domestic_indices_router, prefix="/api", tags=["DomesticIndices"])
router.include_router(rec_stk_router, prefix="/api", tags=["RecStk"])

router.add_api_route("/wordcloud", generate_wordcloud, methods=["GET"])
router.add_api_route("/images/{filename}", get_image, methods=["GET"])
router.add_api_route("/update-stocks", update_stocks_api, methods=["POST"])
router.add_api_route("/update-rt-stk", update_rt_stk, methods=["POST"])
router.add_api_route("/update-domestic-indices", update_indices, methods=["POST"])
router.add_api_route("/update-rec-stk", update_rec_stk, methods=["POST"])


