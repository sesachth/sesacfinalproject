from sqlalchemy.orm import Session
import models.model as model

def get_boxes(db: Session):
    return db.query(model.Box).all()