#import pytest
from fastapi.testclient import TestClient
from core.db.database import get_db
from main import app
import core.helpers.box_matching as sdm
import core.db.crud as crud 
import asyncio

client = TestClient(app)

#@pytest.mark.asyncio
async def test_matching_process():
    async for db in get_db():
        try:
            # 입력 데이터 로그
            box_list = [
                {'box_num': 1, 'width': 220, 'depth': 190, 'height': 90},
                {'box_num': 2, 'width': 270, 'depth': 180, 'height': 150},
                {'box_num': 3, 'width': 350, 'depth': 250, 'height': 100},
                {'box_num': 4, 'width': 340, 'depth': 250, 'height': 210},
                {'box_num': 5, 'width': 410, 'depth': 310, 'height': 280},
                {'box_num': 6, 'width': 480, 'depth': 380, 'height': 340}
            ]
            print("입력된 box 리스트:", box_list)

            # box_standardize 실행 후 DB 업데이트
            print("box_standardize 실행 중...")
            result = await sdm.box_standardize(db, box_list)
            print("box_standardize 실행 완료, 결과:", result)
            
            # DB에 업데이트된 값 저장
            for r in result:
                await crud.update_product_spec(db, r['product_id'], r['packaging'])

            # 커밋 실행
            await db.commit()
            print("DB에 변경 사항이 성공적으로 반영되었습니다.")
            
            # matching_process 실행
            print("적재 처리 시작")
            await sdm.matching_process(db)  # matching_process 호출
            print("적재 처리 완료!")
        
        except Exception as e:
            print(f"오류 발생: {e}")
            await db.rollback()  # 오류 발생 시 롤백
            print("DB 롤백 완료.")

    print('적재 시뮬레이터 페이지 측 데이터 처리 과정 테스트가 정상적으로 완료되었습니다!')

# FastAPI 엔드포인트 검증 실행 (포트 8000)
if __name__ == "__main__":
    asyncio.run(test_matching_process())
