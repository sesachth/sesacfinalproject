from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy.ext.asyncio import AsyncSession
from core.db.database import get_db

import core.db.crud as crud
import core.db.schemas as schemas
import models.model as model

router = APIRouter()

@router.get("/boxes", response_model=schemas.Box)
async def read_boxes(db: AsyncSession = Depends(get_db)):
    db_boxes = await crud.get_boxes(db)
    
    if db_boxes is None:
        raise HTTPException(status_code=404, detail="Box not found")
    
    boxes_list = [
        {
            "spec": model.Box.spec,
            "width": model.Box.width,
            "depth": model.Box.depth,
            "height": model.Box.height,
            "palletId": model.Box.palletId,
            "productId": model.Box.productId,
        }
        for model.Box in db_boxes
    ]

    return JSONResponse(content={"boxes": boxes_list}, media_type="application/json")