package com.dinuw.firstapp

import android.content.Intent
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log.d
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {


    object MyVariables{
        var productsList = ProductsData()
        var filteredList = ProductsData()
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
	private var names = setNames()

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
        println("Storage Space Available: ${checkStorageSpace()} Bytes")
		fetchJSON()
		//setNames()
        loadActivity()
        updateRecycler()
    }

	override fun onRestart() {
		super.onRestart()
		fetchJSON()
		//setNames()
		addFilters()
	}


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
		fetchJSON()
		//setNames()
        addFilters()
    }


    fun loadActivity() {
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, 1)
        }

        getPrice()

        sp = findViewById(R.id.filterByName) as Spinner
        sp2 = findViewById(R.id.filterByPrice) as Spinner

        addFilters()

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
		fetchJSON()
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

		return originalNames.toTypedArray()
    }


    private fun priceButton(productList: MutableList<Product>): MutableList<Product> {
        var newProducts = mutableListOf<Product>()
        if(priceSelected == "All"){
            return productList
        }

        var searchPrice = 0.0

		when (priceSelected) {
			"Under $10" -> {
				searchPrice = 10.0
			}
			"Under $20" -> {
				searchPrice = 20.0
			}
			"Under $30" -> {
				searchPrice = 30.0
			}
			"Under $40" -> {
				searchPrice = 40.0
			}
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
            MyVariables.productsList.products.toList().forEach {
                if (it.event == eventSelected) {
                    newProducts.add(it)
                }
            }
        }else {
            newProducts = MyVariables.productsList.products.toMutableList()
        }
        return newProducts
    }

    private fun checkStorageSpace(): Long{
        val storageManager = applicationContext.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)

		return storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
    }

	private fun fetchJSON() {
		println("Attempting to Fetch JSON")

		val url = "http://10.0.2.2:5000"

		val request = Request.Builder().url(url).build()

		val client = OkHttpClient()
		client.newCall(request).enqueue(object: Callback {
			override fun onResponse(call: Call, response: Response) {
				val body = response?.body?.string()
				val gson = GsonBuilder().create()
				println(body)

				val importedProducts = gson.fromJson(body, Array<Product>::class.java)

				MyVariables.productsList.products = importedProducts.toMutableList()

				var eventNames = appConfig.names.toMutableSet()

				importedProducts.forEach {
					eventNames.add(it.event)
					d("Dinu", it.event)
				}

				names = eventNames.toTypedArray()

			}
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to Execute Request")
				println(e)
			}
		})

	}

}


