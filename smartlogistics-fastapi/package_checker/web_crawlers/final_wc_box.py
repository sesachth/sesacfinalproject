import time
import os
import base64
import requests

from PIL import Image
from io import BytesIO
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys

url = "http://www.google.com"
search_keyword = 'box damaged'

with webdriver.Chrome(service=Service(ChromeDriverManager().install())) as driver:
    driver.implicitly_wait(time_to_wait=5)
    driver.get(url)

    el_search = driver.find_element(By.NAME, 'q')
    el_search.send_keys(search_keyword + Keys.ENTER)

    time.sleep(3)

    el_image_btn = driver.find_element(By.LINK_TEXT, '이미지')
    el_image_btn.click()

    time.sleep(3)

    # 이미지 저장 폴더 생성
    save_folder = 'images'
    if not os.path.exists(save_folder):
        os.makedirs(save_folder)

    # 무한 스크롤
    last_height = driver.execute_script("return document.body.scrollHeight")
    existing_images = set()  # 이미 저장된 이미지의 src를 추적
    image_counter = 0
    test_count = 0

    while True:
        # 페이지 맨 아래로 스크롤
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        
        # 이미지 가져오기
        images = driver.find_elements(By.TAG_NAME, 'img')

        for img in images:
            img_data = None
            src = img.get_attribute('src')
            
            if src and src.startswith('data:image') and src not in existing_images:  # 중복 체크
                existing_images.add(src)  # 이미 저장된 이미지로 추가
                header, base64_data = src.split(',', 1)
                img_data = base64.b64decode(base64_data)
            elif src and src.startswith('https://encrypted-tbn') and src not in existing_images:    # 중복 체크
                existing_images.add(src)  # 이미 저장된 이미지로 추가
                response = requests.get(src)
                if response.status_code == 200:
                    img_data = response.content

            if img_data:
                # 이미지 크기 확인
                image = Image.open(BytesIO(img_data))
                if image.size[0] >= 46 and image.size[1] >= 46 and image_counter < 400:
                    with open(os.path.join(save_folder, f'image_{image_counter}.jpg'), 'wb') as handler:  # 누적된 번호로 저장
                        handler.write(img_data)
                        image_counter += 1  # 이미지 저장 후 번호 증가

        # 페이지 로딩 대기
        time.sleep(2)
        
        # 새 스크롤 높이 계산
        new_height = driver.execute_script("return document.body.scrollHeight")
        
        test_count += 1

        # 스크롤이 더 이상 되지 않으면 종료
        if new_height == last_height:
            break
        
        last_height = new_height

    driver.quit()