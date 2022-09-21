import json
from flask import jsonify, request
from app.models import User, Product
from app import app, db, bcrypt
from flask_jwt_extended import (create_access_token, create_refresh_token, 
                                jwt_required, get_jwt_identity, get_raw_jwt, 
                                jwt_optional)

@app.route("/", methods=['GET'])
@jwt_required
def home():
    jwt_current_user = get_jwt_identity()
    current_user = User.query.filter_by(username=jwt_current_user).first()
    all_products =  Product.query.filter_by(user_id=current_user.id).all()
    #all_products = Product.query.all()
    product_array = []

    for product in all_products:
        temp_dict = {
            "id" : product.id,
            "name" : product.item_name,
            "event" : product.school_event,
            "location" : product.storage_location,
            "quantity" : product.quantity,
            "price" : product.price,
            "picture" : product.pic_location,
            "user_id" : product.user_id
        }
        product_array.append(temp_dict)
        
    return jsonify(product_array)

@app.route('/add_item', methods=['POST'])
@jwt_required
def add_item():
    jwt_current_user = get_jwt_identity()
    current_user = User.query.filter_by(username=jwt_current_user).first()
    req_data = request.get_json()

    name = req_data.get('name')
    event = req_data.get('event')
    location = req_data.get('location')
    quantity = req_data.get('quantity')
    price = req_data.get('price')
    picture = req_data.get('pic_location')
    user_id = current_user.id

    temp_product = Product(item_name=name, school_event=event, storage_location=location, quantity=quantity, price=price, pic_location=picture, user_id=user_id)

    db.session.add(temp_product)
    db.session.commit()

    return jsonify({"status" : "success"}), 200

@app.route('/delete_item', methods=['DELETE'])
@jwt_required
def delete_item():
    jwt_current_user = get_jwt_identity()
    current_user = User.query.filter_by(username=jwt_current_user).first()
    id = request.args.get('id')
    if id != None:
        item_to_delete = Product.query.get(id)
        if item_to_delete.user_id==current_user.id:
            db.session.delete(item_to_delete)
            db.session.commit()
            return jsonify({"status": "item deleted"}), 200 
    
    return jsonify({"status" : "none deleted"})

@app.route('/edit_item', methods=['PUT'])
@jwt_required
def edit_item():
    jwt_current_user = get_jwt_identity()
    current_user = User.query.filter_by(username=jwt_current_user).first()
    req_data = request.get_json()
    id = req_data.get('id')
    if id != None:
        item_to_edit = Product.query.get(id)
        if item_to_edit!=None and item_to_edit.user_id==current_user.id:
            name = req_data.get('name')
            event = req_data.get('event')
            location = req_data.get('location')
            quantity = req_data.get('quantity')
            price = req_data.get('price')
            picture = req_data.get('picture')
            if name!=None: item_to_edit.item_name = name
            if event!=None: item_to_edit.school_event = event
            if location!=None: item_to_edit.storage_location = location
            if quantity!=None: item_to_edit.quantity = quantity
            if price!=None: item_to_edit.price = price
            if picture!=None: item_to_edit.pic_location = picture
            db.session.commit()
            return jsonify({"status": "item edited"}), 200
        else: return jsonify({"status": "item by that ID DNE"}), 200
    else: return jsonify({"status": "nothing changed"}), 200

@app.route('/register', methods=['POST'])
def register():
    req_data = request.get_json()
    username = req_data.get('username')
    password = req_data.get('password')
    hashed_pass = bcrypt.generate_password_hash(password).decode('utf-8')
    user = User.query.filter_by(username=username).first()
    
    status = 'error'
    
    if user==None:
        try:
            user = User(username=username, password=hashed_pass)
            db.session.add(user)
            db.session.commit()
            access_token = create_access_token(identity=username, expires_delta=False)
            status = f'{access_token}'
        except:
            pass
    else:
        status = 'username taken'
    
    return status

@app.route('/login', methods=['POST'])
def login():
    req_data = request.get_json()
    username = req_data.get('username')
    password = req_data.get('password')

    status = 'error'
    user = User.query.filter_by(username=username).first()
    if user and bcrypt.check_password_hash(user.password, password):
        access_token = create_access_token(identity=username, expires_delta=False)
        status = f'{access_token}'

    return status

@app.route('/this_user')
@jwt_optional
def this_user():
    jwt_current_user = get_jwt_identity()
    current_user = User.query.filter_by(username=jwt_current_user).first()
    if current_user!=None:
        user = {
            "id" : current_user.id,
            "username" : current_user.username
        }
        return jsonify(user)
    return "No user logged in"

@app.route('/get_users')
def get_users():
    all_users =  User.query.all()
    user_array = []

    for user in all_users:
        temp_dict = {
            "user_id" : user.id,
            "username" : user.username
        }
        user_array.append(temp_dict)
        
    return jsonify(user_array)
    

@app.route('/get_user')
@jwt_required
def get_user():
    current_user = get_jwt_identity()
    return current_user
