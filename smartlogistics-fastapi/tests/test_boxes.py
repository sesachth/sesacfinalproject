from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_read_boxes():
    response = client.get("/api/v1/boxes")
    assert response.status_code == 200
    assert response.headers["Content-Type"] == "application/json"

    # 응답의 구조와 내용을 더 자세히 검증
    data = response.json()
    assert "boxes" in data
    assert isinstance(data["boxes"], list)

    boxes = data["boxes"]
    assert len(boxes) > 0  # 최소한 하나의 box가 있는지 확인

    # 각 박스의 구조 확인 (예: 각 아이템이 'spec'과 'width' 필드를 가지고 있는지)
    for box in boxes:
        assert "spec" in box
        assert "width" in box
        assert "depth" in box
        assert "height" in box
        assert "palletId" in box
        assert "productId" in box
    
    print('/api/v1/boxes 엔드포인트에 대한 FastAPI 검증 테스트가 정상적으로 완료되었습니다!')

# ✅ FastAPI 엔드포인트 검증 실행 (포트 8000)
if __name__ == "__main__":
    test_read_boxes()