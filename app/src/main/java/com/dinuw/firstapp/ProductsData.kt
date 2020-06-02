package com.dinuw.firstapp

import java.io.Serializable

class ProductsData  : Serializable {


    var products = mutableListOf(
        Product(99, "Forks", "BBQ", "Rm. 104", 5, 9.3, null),
        Product(98, "Napkins", "BBQ", "Rm. 104",25, 4.30, null),
        Product(97, "Soap", "BBQ", "Rm. 104",4, 67.90, null),
        Product(96, "Costume", "Halloween", "Rm. 104",30, 55.55, null),
        Product(95, "Fake Blood", "Halloween", "Rm. 104",45, 25.00, null),
        Product(94, "Speakers", "Carnival", "Eng. Office",2, 39.99, null)
    )

    fun allProducts(): MutableList<Product>{
        return products
    }

}