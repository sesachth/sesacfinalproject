from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db

import core.db.crud as crud
import pandas as pd
import numpy as np
import pickle

async def stacking_process(db: AsyncSession = Depends(get_db)):
    # DB에서 데이터 가져오기
    db_orders = await crud.get_order_with_details(db)

    # SQL 쿼리 결과를 DataFrame으로 변환하기
    df_orders = convert_query_result_to_dataframe(db_orders)

    # DataFrame에 volume 열을 추가하고
    # destination -> volume -> weight 순으로 정렬하기
    sorted_df_orders = add_volume_and_sort_dataframe(df_orders)

    # 적재 최적화 알고리즘 처리 - 알고리즘 동작 안 함!
    #stacked_boxes = stack_boxes_3d(sorted_df_orders)

    print('적재 시뮬레이터 페이지 측 전체 데이터 처리가 완료되었습니다!')

def convert_query_result_to_dataframe(db_orders) -> pd.DataFrame:
    data = []
    for row in db_orders:
        order, product, box = row  # 클래스 이름이 아니라 변수 이름으로 언패킹
        
        item = {
            **order.__dict__,
            **{f'product__{k}': v for k, v in product.__dict__.items()}      # Product 속성에 접두사 추가
        }
        
        # box 객체가 None이 아닌 경우에만 box 정보를 추가
        if box is not None:
            item.update({f'box__{k}': v for k, v in box.__dict__.items()})
        
        data.append(item)

    df_orders = pd.DataFrame(data)

    # 결과 저장
    df_orders.to_csv('df_orders.csv', index=False, encoding='utf-8-sig')
    
    with open('df_orders.pickle', 'wb') as f:
        pickle.dump(df_orders, f)
    
    return df_orders

def add_volume_and_sort_dataframe(df_orders) -> pd.DataFrame:
    # volume 열 추가
    df_orders['volume'] = df_orders['box__width'] * df_orders['box__depth'] * df_orders['box__height']

    # destination 기준 오름차순 정렬 후, 
    # 같은 destination 내에서 volume 기준 내림차순 정렬 후,
    # 같은 volume 내에서 weight 기준 내림차순 정렬
    df_orders = df_orders.sort_values(by=['destination', 'volume', 'product__weight'], ascending=[True, False, False])

    # 결과 저장
    df_orders.to_csv('sorted_df_orders.csv', index=False, encoding='utf-8-sig')

    with open('sorted_df_orders.pickle', 'wb') as f:
        pickle.dump(df_orders, f)

    return df_orders

# 알고리즘 동작 안 함!
def stack_boxes_3d(df_orders, pallet_width=1100, pallet_depth=1100, max_height=2000):
    # 3D 팔레트 초기화
    pallet = np.zeros((max_height, pallet_depth, pallet_width), dtype=int)
    
    stacked_boxes = []
    
    for _, box in df_orders.iterrows():
        width = int(box['box__width'])
        depth = int(box['box__depth'])
        height = int(box['box__height'])
        
        orientations = [(width, depth, height), (depth, width, height)]
        
        stacked = False
        for w, d, h in orientations:
            if w <= pallet_width and d <= pallet_depth and h <= max_height:
                for z in range(0, max_height - h + 1, 10):  # 10mm 단위로 검사
                    for y in range(pallet_depth - d + 1):
                        for x in range(pallet_width - w + 1):
                            if np.all(pallet[z:z+h, y:y+d, x:x+w] == 0):
                                # 박스 적재
                                pallet[z:z+h, y:y+d, x:x+w] = 1
                                stacked_boxes.append({
                                    'order_id': box['order_id'],
                                    'x': x,
                                    'y': y,
                                    'z': z,
                                    'width': w,
                                    'depth': d,
                                    'height': h
                                })
                                stacked = True
                                print('박스 적재 중입니다...')
                                break
                        if stacked:
                            break
                    if stacked:
                        break
            if stacked:
                break
        
        if not stacked:
            print(f"박스를 적재할 공간이 부족합니다: order_id {box['order_id']}")
    
    return stacked_boxes