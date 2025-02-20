from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db
import core.db.crud as crud
import pandas as pd
from itertools import permutations

# 박스 매칭 알고리즘
async def box_standardize(product_list, box_list):
    result_list = []
    box_list_sorted = sorted(box_list, key=lambda x: x['box_num'])

    for product in product_list:
        pw, pd, ph, weight  = (
            product['width'], product['depth'], product['height'], product['weight']
        )
        
        # 무게가 30,000g 초과하면 패키징 제외
        if weight > 30000:
            result_list.append({**product, 'packaging': '패키징 제외'})
            continue
        
        sum_dim = pw + pd + ph
        sorted_dim = sorted([pw, pd, ph])
        min_len, mid_len, max_len = sorted_dim

        cond1 = sum_dim <= 500  # 50cm 이하
        cond2 = (min_len <= 0.2 * max_len) and (mid_len <= 0.2 * max_len)
        cond3 = mid_len >= 4 * min_len
        is_special = cond1 or cond2 or cond3

        found_box = False

        for i, box in enumerate(box_list_sorted):
            box_num, box_w, box_d, box_h = box['box_num'], box['width'], box['depth'], box['height']
            
            for perm in permutations([pw, pd, ph]):
                w, d, h = perm

                if (w < box_w) and (d < box_d) and (h < box_h):
                    diff_w, diff_d, diff_h = box_w - w, box_d - d, box_h - h
                    box_volume = box_w * box_d * box_h
                    product_volume = w * d * h
                    space_ratio = (box_volume - product_volume) / box_volume * 100

                    if is_special:
                        if (diff_w >= 10) and (diff_d >= 10) and (diff_h >= 10):
                            result_list.append({**product, 'packaging': box_num, 'diff_width': diff_w, 
                                                'diff_depth': diff_d, 'diff_height': diff_h, 'space_ratio': space_ratio})
                            found_box = True
                            break

                    if (diff_w >= 10) and (diff_d >= 10) and (diff_h >= 10) and (space_ratio <= 70):
                        result_list.append({**product, 'packaging': box_num, 'diff_width': diff_w, 
                                            'diff_depth': diff_d, 'diff_height': diff_h, 'space_ratio': space_ratio})
                        found_box = True
                        break

            if found_box:
                break  # 박스 선택 완료

        if not found_box:
            result_list.append({**product, 'packaging': '패키징 제외'})

    return result_list

# 박스 매칭 함수
async def matching_process(db: AsyncSession = Depends(get_db)):
    print("박스 매칭 시작")
    
    product_list = await crud.get_products(db)

    box_list = [
        {'box_num': 1, 'width': 220, 'depth': 190, 'height': 90},
        {'box_num': 2, 'width': 270, 'depth': 180, 'height': 150},
        {'box_num': 3, 'width': 350, 'depth': 250, 'height': 100},
        {'box_num': 4, 'width': 340, 'depth': 250, 'height': 210},
        {'box_num': 5, 'width': 410, 'depth': 310, 'height': 280},
        {'box_num': 6, 'width': 480, 'depth': 380, 'height': 340}
    ]

    # 박스 매칭 결과 계산
    result = await box_standardize(product_list, box_list)

    # DB에 결과 업데이트
    for r in result:
        await crud.update_product_spec(db, r['product_id'], r['packaging'])
        
    await db.commit()
