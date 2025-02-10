from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select    # SQLAlchemy 2.0 스타일 쿼리 사용
from models.model import Box

async def get_boxes(db: AsyncSession):
    query = select(Box)                 # ORM 모델을 대상으로 쿼리 작성
    result = await db.execute(query)    # 비동기 실행
    return result.scalars().all()       # 결과를 ORM 객체로 반환