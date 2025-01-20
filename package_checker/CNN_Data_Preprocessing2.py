import os
import cv2
import numpy as np
from sklearn.model_selection import train_test_split
from pathlib import Path
from glob import glob

# ✅ 한글 경로 지원 및 데이터 로딩 함수
def imread_unicode(filepath):
    stream = open(filepath.encode('utf-8'), 'rb')
    bytes_array = bytearray(stream.read())
    np_array = np.asarray(bytes_array, dtype=np.uint8)
    return cv2.imdecode(np_array, cv2.IMREAD_UNCHANGED)

# ✅ 데이터 경로 설정
normal_folder_path = Path('/Users/kenek/labs_python/src_wc/output_image/box_normal_1361')
damaged_folder_path = Path('/Users/kenek/labs_python/src_wc/output_image/box_damaged_1412')
IMAGE_SIZE = (112, 112)

# ✅ 데이터 저장 리스트
x_data_rgb = []
y_data = []

# ✅ 데이터 로딩 및 전처리 (손상 박스 포함)
def load_and_preprocess_images(folder_path, label):
    file_list = glob(str(folder_path / '*.jpg')) + glob(str(folder_path / '*.png'))
    for img_path in file_list:
        try:
            # RGB 변환 및 크기 조정
            img_rgb = imread_unicode(img_path).astype(np.uint8)
            img_rgb = cv2.resize(img_rgb, IMAGE_SIZE)
            img_rgb = img_rgb / 255.0
            x_data_rgb.append(img_rgb)

            # 라벨 추가
            y_data.append(label)
        except Exception as e:
            print(f"⚠️ Error processing {img_path}: {e}")

# ✅ 전체 데이터 로딩 (제한 없음)
load_and_preprocess_images(normal_folder_path, label=0)
load_and_preprocess_images(damaged_folder_path, label=1)

# ✅ 넘파이 배열로 변환 및 섞기
from sklearn.utils import shuffle
x_data_rgb, y_data = shuffle(np.array(x_data_rgb), np.array(y_data), random_state=42)

# ✅ 데이터 분할 (80% 훈련, 20% 테스트)
x_train_rgb, x_test_rgb, y_train, y_test = train_test_split(x_data_rgb, y_data, test_size=0.2, random_state=42)

# ✅ 데이터 저장 (NPY 파일로 저장)
np.save(normal_folder_path / 'x_train_rgb_balanced.npy', x_train_rgb)
np.save(normal_folder_path / 'x_test_rgb_balanced.npy', x_test_rgb)
np.save(normal_folder_path / 'y_train_balanced.npy', y_train)
np.save(normal_folder_path / 'y_test_balanced.npy', y_test)

# ✅ 데이터 크기 출력
print(f"x_train_rgb shape: {x_train_rgb.shape}")
print(f"x_test_rgb shape: {x_test_rgb.shape}")
print(f"y_train shape: {y_train.shape}")
print(f"y_test shape: {y_test.shape}")
