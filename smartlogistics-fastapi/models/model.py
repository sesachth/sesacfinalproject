from sqlalchemy import Column, Integer, Float, ForeignKey
from core.db.database import Base

class Box(Base):
    __tablename__ = "box"

    spec = Column(Integer, primary_key=True, unique=True, index=True, nullable=False)
    width = Column(Float, nullable=False)
    depth = Column(Float, nullable=False)
    height = Column(Float, nullable=False)
    Pallet_palletId = Column(Integer, ForeignKey('pallet.palletId'), index=True)
    product_productId = Column(Integer, ForeignKey('product.productId'), index=True)