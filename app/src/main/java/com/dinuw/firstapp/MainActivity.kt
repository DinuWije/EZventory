package com.dinuw.firstapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log.d
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {


    object MyVariables{
        var productsD = ProductsData()
        var filteredList = ProductsData()
        var picturesList = listOf<Bitmap>()
    }

	object MyFunctions{
		fun roundDouble(number: Double) : String{
			return "%.2f".format(number)
		}
	}

    private val appConfig = AppConfig(this)
    private lateinit var sp : Spinner
    private lateinit var sp2 : Spinner
    private var totalCost = 0.0
    private var eventSelected = "All"
    private var priceSelected = "All"
    private var photoFileNew: String? = null

    private fun roundDouble(number: Double) : String{
        return "%.2f".format(number)
    }

    private fun updateRecycler() {
		recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		recyclerView.setHasFixedSize(true)
        recyclerView.adapter = MainAdapter()

    }

    private fun getPrice(){
        productsTextView.text = ""
        totalCost = 0.0
        MyVariables.filteredList.products.forEach{
            totalCost+=it.price
        }
        lastSavedProduct.text = "$${roundDouble(totalCost)}"
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //d("Dinu", "Storage Space Available: ${checkStorageSpace()} Bytes")
        loadActivity()
        updateRecycler()
    }

	override fun onRestart() {
		super.onRestart()
		MyVariables.filteredList.products.forEach {
			d("Dinu", it.name)
		}
		addFilters()
	}


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MyVariables.productsD = data?.getSerializableExtra("productsData") as ProductsData
        addFilters()
    }


    fun loadActivity() {
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            intent = Intent(this, AddProductActivity::class.java)
            intent.putExtra("productsData", MyVariables.productsD)
            startActivityForResult(intent, 1)
        }

        getPrice()

        sp = findViewById(R.id.filterByName) as Spinner
        sp2 = findViewById(R.id.filterByPrice) as Spinner

        addFilters()

//        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
//
//        val savedName = preferences.getString("savedProductName", "This value doesn't exist")
//        val savedOwner = preferences.getString("savedProductOwner", "This value doesn't exist")
//        val savedYear = preferences.getString("savedProductYear", "This value doesn't exist")
//        val savedPrice = preferences.getString("savedPrice", "This value doesn't exist")
//        d("Dinu", "Name: $savedName \nOwner: $savedOwner\nYear: $savedYear")
    }

    private fun applyFilters() {
        MyVariables.filteredList.products = priceButton(nameButton())

        if (MyVariables.filteredList.products.size > 0) {
            getPrice()
            updateRecycler()
        } else {
            getPrice()
            productsTextView.append("No items to display.")
            updateRecycler()
        }
    }

    private fun addFilters() {
        var names = setNames()
        var prices = appConfig.prices.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, prices)
        sp.adapter = adapter
        sp2.adapter = adapter2


        sp2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                priceSelected = prices[position]
                applyFilters()
            }
        }

        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                eventSelected = names[position]
                applyFilters()
            }

        }
    }

    private fun setNames(): Array<String> {
        var originalNames = appConfig.names.toMutableSet()

        MyVariables.productsD.products.forEach {
            originalNames.add(it.eventSchool)
        }

        var names = originalNames.toTypedArray()
        return names
    }


    private fun priceButton(productList: MutableList<Product>): MutableList<Product> {
        var newProducts = mutableListOf<Product>()
        if(priceSelected == "All"){
            return productList
        }

        var searchPrice = 0.0

        if(priceSelected == "Under $10") {
            searchPrice = 10.0
        }
        else if (priceSelected == "Under $20") {
            searchPrice = 20.0
        }
        else if (priceSelected == "Under $30") {
            searchPrice = 30.0
        }
        else if (priceSelected == "Under $40") {
            searchPrice = 40.0
        }

        productList.forEach {
            if(it.price < searchPrice){
                newProducts.add(it)
            }

        }
        return newProducts
    }

    private fun nameButton(): MutableList<Product> {
        var newProducts = mutableListOf<Product>()
        if(eventSelected != "All") {
            MyVariables.productsD.products.toList().forEach {
                if (it.eventSchool == eventSelected) {
                    newProducts.add(it)
                }
            }
        }else {
            newProducts = MyVariables.productsD.products.toMutableList()
        }
        return newProducts
    }

    private fun checkStorageSpace(): Long{
        val storageManager = applicationContext.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)

        return availableBytes
    }

}
