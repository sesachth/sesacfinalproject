from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db
import core.db.crud as crud 
from core.helpers.box_matching import box_standardize

router = APIRouter()

@router.post("/box-matching")
async def match_box(product: dict, db: AsyncSession = Depends(get_db)):
    # 단일 상품을 리스트로 변환
    product_list = [product]

    # 박스 리스트는 기존 box_matching.py와 동일한 형태로 유지
    box_list = [
        {'box_num': 1, 'width': 220, 'depth': 190, 'height': 90},
        {'box_num': 2, 'width': 270, 'depth': 180, 'height': 150},
        {'box_num': 3, 'width': 350, 'depth': 250, 'height': 100},
        {'box_num': 4, 'width': 340, 'depth': 250, 'height': 210},
        {'box_num': 5, 'width': 410, 'depth': 310, 'height': 280},
        {'box_num': 6, 'width': 480, 'depth': 380, 'height': 340}
    ]

    # 박스 매칭 실행
    spec_result = box_standardize(product_list, box_list)

    # 매칭 결과 DB 저장
    if spec_result:
        await crud.update_product_spec(db, product['product_id'], spec_result[0]['packaging'])
        await db.commit()

    return spec_result[0]
