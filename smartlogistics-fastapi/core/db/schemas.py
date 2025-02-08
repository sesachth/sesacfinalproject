from pydantic import BaseModel

class BoxBase(BaseModel):
    spec: int
    width: float
    depth: float
    height: float
    palletId: int
    productId: int

class Box(BoxBase):
    class Config:
        from_attributes = True