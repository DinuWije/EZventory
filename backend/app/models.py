from app import db, config
from sqlalchemy import Column, String, Integer, ForeignKey
from sqlalchemy.orm import relationship
import pymysql
class User(db.Model):
    __tablename__ = 'users'

    id = Column('id', Integer, primary_key=True)
    username = Column('username', String(120), nullable=False, unique=True) 
    password = Column('password', String(60), nullable=False)
    #todo can set lazy=True
    products = relationship("Product", back_populates='user')

class Product(db.Model):
    __tablename__ = 'products'

    id = Column('id', Integer, primary_key=True)
    item_name = Column('name', String(300), nullable=False, default="N/A")
    school_event = Column('event', String(300), nullable=False, default="N/A")
    storage_location = Column('location', String(300), nullable=False, default="N/A")
    quantity = Column('quantity', Integer, nullable=False, default=0)
    price = Column('price', Integer, nullable=False, default=0)
    pic_location = Column('picture', String(500), nullable=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    user = relationship("User", back_populates="products")

db.create_all()
db.session.commit()