from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db

import core.db.crud as crud
import pandas as pd
import pickle

async def stacking_process(db: AsyncSession = Depends(get_db)):
    # DB에서 데이터 가져오기
    db_orders = await crud.get_order_with_details(db)

    # SQL 쿼리 결과를 DataFrame으로 변환하기
    df_orders = convert_query_result_to_dataframe(db_orders)

    # DataFrame에 volume 열을 추가하고
    # destination -> volume -> weight 순으로 정렬하기
    sorted_df_orders = add_volume_and_sort_dataframe(df_orders)

    # 적재 최적화 알고리즘 처리
    stacking_results = stack_boxes_heuristic(sorted_df_orders)

    print('적재 시뮬레이터 페이지 측 전체 데이터 처리가 완료되었습니다!')

    return stacking_results

def convert_query_result_to_dataframe(db_orders) -> pd.DataFrame:
    data = []
    for row in db_orders:
        order, product, box = row  # 클래스 이름이 아니라 변수 이름으로 언패킹

        # box 객체가 None이 아닌 경우에만 order, product, box 정보를 추가
        if box is not None:        
            item = {
                **order.__dict__,
                **{f'product__{k}': v for k, v in product.__dict__.items()},      # Product 속성에 접두사 추가
                **{f'box__{k}': v for k, v in box.__dict__.items()}
            }

            data.append(item)

    df_orders = pd.DataFrame(data)

    # 결과 저장
    #df_orders.to_csv('df_orders.csv', index=False, encoding='utf-8-sig')
    
    #with open('df_orders.pickle', 'wb') as f:
    #    pickle.dump(df_orders, f)
    
    return df_orders

def add_volume_and_sort_dataframe(df_orders) -> pd.DataFrame:
    # volume 열 추가
    df_orders['volume'] = df_orders['box__width'] * df_orders['box__depth'] * df_orders['box__height']

    # destination 기준 오름차순 정렬 후, 
    # 같은 destination 내에서 volume 기준 내림차순 정렬 후,
    # 같은 volume 내에서 weight 기준 내림차순 정렬
    df_orders = df_orders.sort_values(by=['destination', 'volume', 'product__weight'], ascending=[True, False, False])

    # 결과 저장
    #df_orders.to_csv('sorted_df_orders.csv', index=False, encoding='utf-8-sig')

    #with open('sorted_df_orders.pickle', 'wb') as f:
    #    pickle.dump(df_orders, f)

    return df_orders

def stack_boxes_heuristic(df_orders, pallet_width=1100, pallet_depth=1100, max_height=2000):
    size_counts = df_orders['volume'].value_counts()
    most_common_size = size_counts.index[0]
    
    df_filtered = df_orders[df_orders['volume'] == most_common_size].copy()
    
    print(f"선택된 박스 크기: {most_common_size}, 개수: {len(df_filtered)}")
    
    stacked_boxes = []
    occupied_spaces = set()  # 이미 사용된 공간을 추적

    box_width, box_depth, box_height = df_filtered.iloc[0][['box__width', 'box__depth', 'box__height']].astype(int)
    
    for z in range(0, max_height, box_height):
        for y in range(0, pallet_depth, box_depth):
            for x in range(0, pallet_width, box_width):
                if (x + box_width <= pallet_width and 
                    y + box_depth <= pallet_depth and 
                    z + box_height <= max_height):
                    
                    space_key = (x, y, z)
                    if space_key not in occupied_spaces:
                        occupied_spaces.add(space_key)
                        
                        if len(stacked_boxes) < len(df_filtered):
                            box_data = df_filtered.iloc[len(stacked_boxes)]
                            stacked_boxes.append({
                                "product_name": box_data['product__name'],
                                "width": box_width / 1000,  # mm를 m로 변환
                                "depth": box_depth / 1000,
                                "height": box_height / 1000,
                                "x_coordinate": x / 1000,
                                "y_coordinate": z / 1000,
                                "z_coordinate": y / 1000
                            })
                        else:
                            break
            if len(stacked_boxes) == len(df_filtered):
                break
        if len(stacked_boxes) == len(df_filtered):
            break

    # 결과를 요청한 형식으로 구성
    stacking_results = {
        "pallets": [
            {
                "pallet_id": 0,
                "destination": df_filtered.iloc[0]['destination'],
                "boxes": stacked_boxes
            }
        ]
    }

    # CSV 파일로 저장
    #pd.DataFrame(stacked_boxes).to_csv('stacked_boxes.csv', index=False, encoding='utf-8-sig')

    return stacking_results