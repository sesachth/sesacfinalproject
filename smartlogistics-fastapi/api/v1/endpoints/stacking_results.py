from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db
from core.db.schemas import CustomJSONEncoder

import core.helpers.stacking_data_manager as sdm
import core.db.crud as crud
import core.db.schemas as schemas
import models.model as model
import json

router = APIRouter()

@router.get("/stacking_results")
async def read_stacking_results(db: AsyncSession = Depends(get_db)):
    # DB에서 데이터 가져오기
    db_orders = await crud.get_order_with_details(db)

    # SQL 쿼리 결과를 DataFrame으로 변환하기
    df_orders = sdm.convert_query_result_to_dataframe(db_orders)

    # DataFrame에 volume 열을 추가하고
    # destination -> volume -> weight 순으로 정렬하기
    sorted_df_orders = sdm.add_volume_and_sort_dataframe(df_orders)

    # 적재 최적화 알고리즘 처리
    stacking_results = sdm.stack_boxes_heuristic(sorted_df_orders)

    # 적재 최적화 결과를 order 테이블에 업데이트
    await crud.update_order_after_stacking(stacking_results, db)

    # 적재 최적화 결과를 pallet 테이블에 업데이트
    await crud.update_pallet_after_stacking(stacking_results, db)

    return {"meesage": "OK"}

@router.get("/order_with_details")
async def read_stacking_results(db: AsyncSession = Depends(get_db)):
    db_orders = await crud.get_order_with_details(db)

    if db_orders is None:
        raise HTTPException(status_code=404, detail="Order not found")

    print(db_orders)

    # ORM 객체를 Pydantic 모델로 변환
    orders_data = [
        schemas.OrderWithDetailsModel(
            order=schemas.OrderModel.model_validate(model.Order),
            product=schemas.ProductModel.model_validate(model.Product),
            box=schemas.BoxModel.model_validate(model.Box) if model.Box else None
        )
        for model.Order, model.Product, model.Box in db_orders
    ]

    # CustomJSONEncoder를 사용하여 JSON 직렬화
    json_data = json.dumps([model.model_dump() for model in orders_data], cls=CustomJSONEncoder)

    return JSONResponse(content=json.loads(json_data))