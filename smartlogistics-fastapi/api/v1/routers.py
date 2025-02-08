from fastapi import APIRouter
from api.v1.endpoints import box

api_router = APIRouter()
api_router.include_router(box.router, tags=["boxes"])