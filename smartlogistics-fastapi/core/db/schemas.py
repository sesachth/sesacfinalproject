from pydantic import BaseModel

class Box(BaseModel):
    spec: int
    width: float
    depth: float
    height: float
    palletId: int
    productId: int

    class Config:
        from_attributes = True