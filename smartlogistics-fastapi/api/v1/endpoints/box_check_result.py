from fastapi import APIRouter
from core.db.schemas import BoxCheckRequest
from core.helpers.box_check import predict_single_image
from pathlib import Path
import logging
import requests


router = APIRouter()

@router.post("/box_check/")
async def predict_image(request: BoxCheckRequest):
    try:
        image_path = Path('static/boximages') / f"{request.image_number:03d}.jpg"
        logging.info(f"📦 요청된 order_id: {request.order_id}, image_number: {request.image_number:03d}")
        
        if not image_path.exists():
            return {"error": "Image not found."}
        
        prediction = predict_single_image(image_path)  
        box_state = 2 if prediction > 0.5 else 1  # Damaged: 2, Normal: 1
        
        # ✅ Spring Boot WebSocket 서버로 전송
        websocket_payload = {
            "orderId": request.order_id,
            "boxState": box_state,
        }
        
        response = requests.post(
                "http://localhost:80/api/v1/update-box-state",
                json=websocket_payload,
                headers={'Content-Type': 'application/json'}
            )
        response.raise_for_status()
        
        
        logging.info(f"Spring Boot 서버 응답: {response.status_code}")
        
        logging.info(f"Spring Boot 서버 응답 본문: {response.text}")
        
        return {"boxState": box_state}
  
    except Exception as e:
        logging.error(f"오류 발생: {str(e)}")
        return {"error": str(e)}, 500