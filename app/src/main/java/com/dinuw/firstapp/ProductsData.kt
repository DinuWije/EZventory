package com.dinuw.firstapp

import java.io.Serializable

class ProductsData  : Serializable {

    var products = mutableListOf<Product>()

    fun allProducts(): MutableList<Product>{
        return products
    }

}
