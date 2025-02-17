from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select    # SQLAlchemy 2.0 스타일 쿼리 사용
from sqlalchemy import text
from models.model import Order, Product, Box

async def get_boxes(db: AsyncSession):
    query = select(Box)                 # ORM 모델을 대상으로 쿼리 작성
    result = await db.execute(query)    # 비동기 실행
    return result.scalars().all()       # 결과를 ORM 객체로 반환

async def get_order_with_details(session: AsyncSession):
    stmt = (
        select(Order, Product, Box)
        .join(Product, Order.product_id == Product.product_id)
        .outerjoin(Box, Product.spec == Box.spec)
    )
    result = await session.execute(stmt)
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