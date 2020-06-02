package com.dinuw.firstapp

import android.util.Log.d


class AppConfig(mainAct: MainActivity) {


//    mainAct.productsD.products.forEach{
//        d("Dinu", "${it.eventSchool}")
//    }

    var names = mutableSetOf(
        "All"
    )
    var prices = mutableSetOf("All", "Under $10", "Under $20", "Under $30", "Under $40")
}