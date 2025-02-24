from fastapi import APIRouter
from typing import Dict
from core.db.schemas import ProductModel
from core.helpers.box_matching import box_standardize
import logging

router = APIRouter()

# 로거 설정
logger = logging.getLogger(__name__)

@router.post("/box_matching")
async def match_box(product: ProductModel) -> Dict[str, int]:
    logger.info("Starting box matching for product: %s", product.name)  # 로깅 추가
    # 박스 매칭 알고리즘 실행
    box_list = [
        {'box_num': 1, 'width': 220, 'depth': 190, 'height': 90},
        {'box_num': 2, 'width': 270, 'depth': 180, 'height': 150},
        {'box_num': 3, 'width': 350, 'depth': 250, 'height': 100},
        {'box_num': 4, 'width': 340, 'depth': 250, 'height': 210},
        {'box_num': 5, 'width': 410, 'depth': 310, 'height': 280},
        {'box_num': 6, 'width': 480, 'depth': 380, 'height': 340}
    ]

    # Product 객체를 dict로 변환하여 매칭 수행
    product_list = [product.model_dump()]
    spec_result = await box_standardize(product_list, box_list)

    if spec_result:
        logger.info("Box matching successful. Matched box: %s", spec_result[0]['packaging'])
        return {"spec": spec_result[0]['packaging']}  # spec 값 반환
    else:
        logger.warning("No matching box found for product: %s", product.name)  # 매칭 실패 로그
        return {"spec": None}  # 매칭 실패 시 기본 값 반환