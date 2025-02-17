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

    print(df_orders)

    '''
    # volume 열 추가
    df_orders['volume'] = df_orders['width'] * df_orders['depth'] * df_orders['height']

    # destination 열 기반 그룹핑
    grouped_by_destination = df_orders.groupby('destination')
    
    # volume 기준 내림차순 정렬 후, 같은 volume 내에서 weight 기준 내림차순 정렬
    volume_weight_sorted = grouped_by_destination.apply(
        lambda x: x.sort_values(by=['volume', 'weight'], ascending=[False, False])
    )

    # 결과 저장 (비동기로 처리)
    await pickle.dump(grouped_by_destination.groups, 'grouped_orders.pickle')
    await pickle.dump(volume_weight_sorted, 'volume_weight_sorted_orders.pickle')
    '''
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
    
    return pd.DataFrame(data)