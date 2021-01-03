import os
from flask import Flask, jsonify
from sqlalchemy import create_engine
from sqlalchemy.engine.url import URL
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_jwt_extended import JWTManager

config = {
    'host': 'mysql-development',
    'port': 3306,
    'user': 'root',
    'password': 'password',
    'database': 'inventory_db'
}

db_user = config.get('user')
db_pwd = config.get('password')
db_host = config.get('host')
db_port = config.get('port')
db_name = config.get('database')

# specify connection string
database_file = f'mysql+pymysql://{db_user}:{db_pwd}@{db_host}:{db_port}/{db_name}'

# SQLite db:
# project_dir = os.path.dirname(os.path.abspath(__file__))
# database_file = 'sqlite:///{}'.format(os.path.join(project_dir, "productdatabase.db"))

app = Flask(__name__)
app.config['SECRET_KEY'] = '5791628bb0b13ce0c676dfde280ba245'
app.config['SQLALCHEMY_DATABASE_URI'] = database_file
app.config['JSON_SORT_KEYS'] = False
app.config['JWT_SECRET_KEY'] = 'jwt-secret-string'

db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
jwt = JWTManager(app)

# connect to database
engine = db.create_engine(database_file, {})
connection = engine.connect()

from app import routes