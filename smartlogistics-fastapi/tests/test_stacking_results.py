from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_read_stacking_results():
    response = client.get("/api/v1/stacking_results")
    assert response.status_code == 200
    assert response.headers["Content-Type"] == "application/json"

    # 응답의 구조와 내용을 더 자세히 검증
    data = response.json()
    assert "pallets" in data
    assert isinstance(data["pallets"], list)

    pallets = data["pallets"]
    assert len(pallets) > 0  # 최소한 하나의 pallet가 있는지 확인

    # 각 pallet의 구조 확인 (예: 각 아이템이 'destination'과 'boxes' 필드를 가지고 있는지)
    for pallet in pallets:
        assert "destination" in pallet
        assert "boxes" in pallet
        assert isinstance(pallet["boxes"], list)

        boxes = pallet["boxes"]
        assert len(boxes) > 0

        # boxes의 구조와 내용을 더 자세히 검증
        for box in boxes:
            assert "product_name" in box
            assert "width" in box
            assert "depth" in box
            assert "height" in box
            assert "x_coordinate" in box
            assert "y_coordinate" in box
            assert "z_coordinate" in box

    print('/api/v1/stacking_results 엔드포인트에 대한 FastAPI 검증 테스트가 정상적으로 완료되었습니다!')

# ✅ FastAPI 엔드포인트 검증 실행 (포트 8000)
if __name__ == "__main__":
    test_read_stacking_results()