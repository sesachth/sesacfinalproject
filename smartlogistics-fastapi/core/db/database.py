from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from typing import AsyncGenerator
from config import settings

SQLALCHEMY_DATABASE_URL = str(settings.DATABASE_URL)

engine = create_async_engine(
    SQLALCHEMY_DATABASE_URL,
    pool_size=10,           # 연결 풀 크기
    max_overflow=20,        # 최대 초과 연결 수
    pool_recycle=3600,      # 연결 재활용 시간 (초)
    echo=True,              # 디버깅용 로그 출력 (필요 시 False로 설정)
)

# AsyncSession 생성
async_session = sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False  # 세션 커밋 후 객체가 만료되지 않도록 설정
)

Base = declarative_base()

# 비동기 세션 제공 함수
async def get_db() -> AsyncGenerator[AsyncSession, None]:
    async with async_session() as session:
        try:
            yield session
        finally:
            await session.close()