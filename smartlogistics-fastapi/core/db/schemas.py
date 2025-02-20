from datetime import datetime
from pydantic import BaseModel, Field
from typing import Optional

import json

class CustomJSONEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.isoformat()
        return super().default(obj)

class OrderModel(BaseModel):
    order_id: int
    order_num: str = Field(max_length=14)
    order_time: datetime
    destination: str = Field(max_length=45)
    box_state: int
    progress_state: int
    product_id: int
    pallet_id: Optional[int] = None

    class Config:
        from_attributes = True

class ProductModel(BaseModel):
    product_id: Optional[int] = None
    name: str = Field(max_length=45)
    width: float
    depth: float
    height: float
    weight: float
    is_fragile: bool 
    category: str = Field(max_length=30)
    spec: Optional[int] = None

    class Config:
        from_attributes = True

class BoxModel(BaseModel):
    spec: int
    width: float
    depth: float
    height: float

    class Config:
        from_attributes = True

class PalletModel(BaseModel):
    pallet_id: int
    load: Optional[float] = None
    width: float
    depth: float
    height: Optional[float] = None
    destination: Optional[str] = Field(max_length=20)
    vehicle_number: Optional[str] = Field(max_length=45)

    class Config:
        from_attributes = True

class OrderWithDetailsModel(BaseModel):
    order: OrderModel
    product: ProductModel
    box: Optional[BoxModel] = None