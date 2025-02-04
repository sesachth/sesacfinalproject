import os
import cv2
import xml.etree.ElementTree as ET

# 폴더 경로 설정
annotation_dir = "xml"  # .xml 파일들이 있는 폴더
image_dir = "images"            # 대응되는 .jpg 이미지들이 있는 폴더
output_folder = "7_cropped_images"

if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# 클래스 이름을 숫자로 매핑하기 위한 딕셔너리
label_map = {
    "Damaged": 1,
    "damaged": 1,
    "Normal": 0,
    "normal": 0,
}

# annotations 폴더 내 모든 .xml 파일 목록
xml_files = [f for f in os.listdir(annotation_dir) if f.endswith(".xml")]

for xml_file in xml_files:
    # (1) .xml 파일의 베이스 이름 추출 (확장자 제거)
    base_name = os.path.splitext(xml_file)[0]

    # (2) 이미지 파일(.jpg) 경로 만들기
    image_path_jpg = os.path.join(image_dir, base_name + ".jpg")

    # (3) 이미지가 존재하는지 확인
    if not os.path.exists(image_path_jpg):
        print(f"[SKIP] No matching .jpg for XML: {xml_file}")
        continue  # 매칭되는 이미지 없으면 무시하고 다음 .xml 진행

    # (4) 이미지 로드
    img = cv2.imread(image_path_jpg)
    if img is None:
        print(f"[SKIP] Could not read image: {image_path_jpg}")
        continue

    # (5) XML 파싱
    xml_path = os.path.join(annotation_dir, xml_file)
    tree = ET.parse(xml_path)
    root = tree.getroot()

    # 한 XML 안에 여러 object가 있을 수 있으므로 반복
    obj_idx = 0
    for obj in root.findall('object'):
        name = obj.find('name').text  # 클래스 이름
        bndbox = obj.find('bndbox')
        xmin = int(bndbox.find('xmin').text)
        ymin = int(bndbox.find('ymin').text)
        xmax = int(bndbox.find('xmax').text)
        ymax = int(bndbox.find('ymax').text)

        # (6) 바운딩 박스 Cropping
        cropped_img = img[ymin:ymax, xmin:xmax]

        # (7) 라벨 매핑 (만약 딕셔너리에 없으면 -1로)
        label_id = label_map.get(name, -1)

        # (8) 크롭된 이미지 저장 경로 지정
        out_name = f"{base_name}_{obj_idx}_{label_id}.jpg"
        out_path = os.path.join(output_folder, out_name)

        # (9) 크롭된 이미지 파일 저장
        cv2.imwrite(out_path, cropped_img)
        print(f"[SAVE] {out_path}")

        obj_idx += 1
