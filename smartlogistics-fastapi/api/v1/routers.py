from fastapi import APIRouter
from api.v1.endpoints import box, stacking_results

api_router = APIRouter()
api_router.include_router(box.router, tags=["boxes"])
api_router.include_router(stacking_results.router, tags=["stacking_results", "order_with_details"])