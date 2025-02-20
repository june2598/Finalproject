from fastapi import APIRouter
from app.api.endpoints.wordcloud_trend import generate_wordcloud, generate_wordcloud2, get_image

router = APIRouter()

# 엔드포인트 등록
router.add_api_route("/wordcloud", generate_wordcloud, methods=["GET"])
router.add_api_route("/wordcloud2", generate_wordcloud2, methods=["GET"])
router.add_api_route("/images/{filename}", get_image, methods=["GET"])