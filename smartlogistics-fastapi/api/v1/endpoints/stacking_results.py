from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db
from core.db.schemas import CustomJSONEncoder

import core.db.crud as crud
import core.db.schemas as schemas
import models.model as model
import json

router = APIRouter()

@router.get("/stacking_results")
async def read_stacking_results():
    stacking_results = {
        "pallets": [
        {
            "pallet_id": 0,
            "destination": "서초 캠프",
            "boxes": [{
                "product_name": "test1",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0,
                "y_coordinate": 0.15,
                "z_coordinate": 0
            },
            {
                "product_name": "test2",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.1,
                "y_coordinate": 0.15,
                "z_coordinate": 0.1
            },
            {
                "product_name": "test3",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.2,
                "y_coordinate": 0.15,
                "z_coordinate": 0.2
            }]
        },
        {
            "pallet_id": 1,
            "destination": "강남 캠프",
            "boxes": [{
                "product_name": "test4",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.3,
                "y_coordinate": 0.15,
                "z_coordinate": 0.3
            },
            {
                "product_name": "test5",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.4,
                "y_coordinate": 0.15,
                "z_coordinate": 0.4
            },
            {
                "product_name": "test6",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.5,
                "y_coordinate": 0.15,
                "z_coordinate": 0.5
            }]
        },
        {
            "pallet_id": 2,
            "destination": "강서 캠프",
            "boxes": [{
                "product_name": "test7",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.6,
                "y_coordinate": 0.15,
                "z_coordinate": 0.6
            },
            {
                "product_name": "test8",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.7,
                "y_coordinate": 0.15,
                "z_coordinate": 0.7
            },
            {
                "product_name": "test9",
                "width": 0.1,
                "depth": 0.1,
                "height": 0.1,
                "x_coordinate": 0.8,
                "y_coordinate": 0.15,
                "z_coordinate": 0.8
            }]
        }
    ]}

    return JSONResponse(content=stacking_results, media_type="application/json")

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