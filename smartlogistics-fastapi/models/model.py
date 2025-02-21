from sqlalchemy import Column, Boolean, Integer, Float, String, DateTime, ForeignKey
from core.db.database import Base

class Order(Base):
    __tablename__ = "order"

    order_id = Column(Integer, primary_key=True, unique=True, index=True, nullable=False)
    order_num = Column(String(14), nullable=False)
    order_time = Column(DateTime, nullable=False)
    destination = Column(String(45), nullable=False)
    box_state = Column(Integer, nullable=False)
    progress_state = Column(Integer, nullable=False)
    product_id = Column(Integer, ForeignKey('product.product_id'), index=True, nullable=False)
    pallet_id = Column(Integer, ForeignKey('pallet.pallet_id'), index=True)
    seq_stacking = Column(Integer)
    x_coordinate = Column(Float)
    y_coordinate = Column(Float)
    z_coordinate = Column(Float)

class Product(Base):
    __tablename__ = "product"

    product_id = Column(Integer, primary_key=True, unique=True, index=True, nullable=False)
    name = Column(String(45), nullable=False)
    width = Column(Float, nullable=False)
    depth = Column(Float, nullable=False)
    height = Column(Float, nullable=False)
    weight = Column(Float, nullable=False)
    is_fragile = Column(Boolean, nullable=False)
    category = Column(String(30), nullable=False)
    spec = Column(Integer, ForeignKey('box.spec'), index=True)

class Box(Base):
    __tablename__ = "box"

    spec = Column(Integer, primary_key=True, unique=True, index=True, nullable=False)
    width = Column(Float, nullable=False)
    depth = Column(Float, nullable=False)
    height = Column(Float, nullable=False)

class Pallet(Base):
    __tablename__ = "pallet"

    pallet_id = Column(Integer, primary_key=True, unique=True, index=True, nullable=False)
    load = Column(Float)
    width = Column(Float, nullable=False)
    depth = Column(Float, nullable=False)
    height = Column(Float)
    destination = Column(String(20))
    vehicle_number = Column(String(45), ForeignKey('vehicle.vehicle_number'), index=True)