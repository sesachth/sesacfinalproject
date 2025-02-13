from fastapi import APIRouter
from fastapi.responses import JSONResponse

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
