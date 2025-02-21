from models.model import Order, Product, Box, Pallet
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select    # SQLAlchemy 2.0 스타일 쿼리 사용
from sqlalchemy import text, update, func
from datetime import date

# box 테이블 내 모든 레코드 가져오기 (테스트 전용, 비동기)
async def get_boxes(db: AsyncSession):
    query = select(Box)                 # ORM 모델을 대상으로 쿼리 작성
    result = await db.execute(query)    # 비동기 실행
    return result.scalars().all()       # 결과를 ORM 객체로 반환

# oder/product/box 테이블 3개를 조인한 모든 레코드 가져오기 (비동기)
async def get_order_with_details(db: AsyncSession):
    today = date.today()
    stmt = (
        select(Order, Product, Box)
        .join(Product, Order.product_id == Product.product_id)
        .outerjoin(Box, Product.spec == Box.spec)
        .where(func.date(Order.order_time) == today)
    )
    result = await db.execute(stmt)
    return result.all()

# 제품 정보 가져오기 (비동기)
async def get_products(db: AsyncSession):
    query = select(Product.product_id, Product.width, Product.depth, Product.height, Product.weight, Product.is_fragile)
    result = await db.execute(query)
    products = result.fetchall()
    return [
        {'product_id': p[0], 'width': p[1], 'depth': p[2], 'height': p[3], 'weight': p[4], 'fragile': p[5]}
        for p in products
    ]

# 제품 spec 업데이트 (비동기)
async def update_product_spec(db: AsyncSession, product_id: int, spec: str):
    if spec != '패키징 제외':
        await db.execute(
            text("UPDATE product SET spec = :spec WHERE product_id = :product_id"),
            {'spec': spec, 'product_id': product_id}
        )
    else:
        await db.execute(
            text("UPDATE product SET spec = NULL WHERE product_id = :product_id"),
            {'product_id': product_id}
        )
        
    await db.commit()

# order 테이블 업데이트 (비동기)
async def update_order_after_stacking(stacking_results: dict, db: AsyncSession):
    try:
        # 1. order 테이블 업데이트
        for pallet in stacking_results["pallets"]:
            for box in pallet["boxes"]:
                stmt = update(Order).where(Order.order_id == box["order_id"]).values(
                    pallet_id=pallet["pallet_id"],
                    seq_stacking=box["seq_stacking"],
                    x_coordinate=box["x_coordinate"],
                    y_coordinate=box["y_coordinate"],
                    z_coordinate=box["z_coordinate"]
                )
                await db.execute(stmt)

        # 변경사항 커밋
        await db.commit()
        
        return {"message": "Database updated successfully"}
    
    except Exception as e:
        # 오류 발생 시 롤백
        await db.rollback()
        raise Exception(f"An error occurred: {str(e)}")
    
# pallet 테이블 업데이트 (비동기)
async def update_pallet_after_stacking(stacking_results: dict, db: AsyncSession):
    try:
        # 2. pallet 테이블 업데이트
        for pallet in stacking_results["pallets"]:
            stmt = update(Pallet).where(Pallet.pallet_id == pallet["pallet_id"]).values(
                load=pallet["load"],
                height=pallet["height"],
                destination=pallet["destination"]
            )
            await db.execute(stmt)

        # 변경사항 커밋
        await db.commit()
        
        return {"message": "Database updated successfully"}
    
    except Exception as e:
        # 오류 발생 시 롤백
        await db.rollback()
        raise Exception(f"An error occurred: {str(e)}")