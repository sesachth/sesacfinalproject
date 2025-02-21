import numpy as np
import tensorflow as tf
import cv2
import matplotlib.pyplot as plt
from pathlib import Path

# ✅ 모델 및 데이터 경로 설정
base_path = Path('models/ml_models')
model_rgb = tf.keras.models.load_model(str(base_path / 'trained_model_best0.h5'))

# ✅ 이미지 불러오기 및 전처리 함수
def load_and_preprocess_image(img_path):
    img = cv2.imread(str(img_path))  # 이미지를 읽기
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)  # BGR에서 RGB로 변환
    img = cv2.resize(img, (112, 112))  # 이미지를 112x112 크기로 리사이즈
    img = img / 255.0  # 이미지를 0~1 사이로 정규화
    return img

# ✅ 단일 이미지 예측 및 시각화 함수
def predict_single_image(image_path):
    image_path = Path(image_path)
    
    test_image = load_and_preprocess_image(image_path)  # 이미지 불러오기 및 전처리
    test_image_batch = np.expand_dims(test_image, axis=0)  # 배치 차원 추가
    
    # 예측 수행
    y_pred_probs = model_rgb.predict(test_image_batch)
    y_pred_label = 'Damaged' if y_pred_probs[0][0] > 0.5 else 'Normal'  # 예측된 레이블
    
    # 이미지 시각화 및 예측 결과 표시
    #plt.figure(figsize=(5, 5))
    #plt.imshow(test_image)
    #plt.title(f"{image_path.name}\n{y_pred_label} ({y_pred_probs[0][0]:.4f})", #fontsize=12)
    #plt.axis('off')  # 축 숨기기
    #plt.show()
    
    # 텍스트 결과 출력
    print(f"Image: {image_path.name}")
    print(f"Prediction: {y_pred_label} ({y_pred_probs[0][0]:.4f})")
    
    return y_pred_probs[0][0]

# ✅ 이미지 예측 실행
#if __name__ == "__main__":
#    image_file = Path('static/boximages') / '001.jpg'  # 예측할 이미지 파일 경로
#   predict_single_image(image_file)
