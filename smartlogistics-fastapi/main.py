from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from api.v1.routers import api_router
from config import settings

app = FastAPI(title=settings.PROJECT_NAME)

# ✅ CORS 설정 (React, Spring Boot 연동)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    max_age=3600
)

# ✅ API 라우터 추가
app.include_router(api_router, prefix="/api/v1")

# ✅ FastAPI 실행 (포트 8000)
if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)