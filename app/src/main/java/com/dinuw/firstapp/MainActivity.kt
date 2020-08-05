package com.dinuw.firstapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    object MyVariables{
        var productsList = ProductsData()
        var filteredList = ProductsData()
		var accessToken:String? = null
		//const val host = "10.0.2.2"
		const val host = "18.224.7.6"
		const val sharedPrefsName = "MySharedPrefs"
    }

	object MyFunctions{
		fun roundDouble(number: Double) : String{
			return "%.2f".format(number)
		}
	}

    private val appConfig = AppConfig(this)
    private lateinit var sp : Spinner
    private lateinit var sp2 : Spinner
	private var names: Array<String> = appConfig.names.toTypedArray()
	private var locations: Array<String> = appConfig.locations.toTypedArray()
    private var totalCost = 0.0
    private var eventSelected = "ALL"
    private var locationSelected = "ALL"

	private val scheme = "http"
	private val host = MyVariables.host
	private val client = OkHttpClient()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		if (checkFirstTime()){
			loadStartupPage()
		} else {
			val tempLoginStatus = checkloginStatus()
			if (tempLoginStatus!=null){
				MyVariables.accessToken=tempLoginStatus
				loadHomepage()
			} else loadStartupPage()
		}
    }

	private fun checkFirstTime():Boolean{

		val prefsName = MyVariables.sharedPrefsName

		val sharedPrefs = getSharedPreferences(prefsName, 0)

		if (sharedPrefs.getBoolean("my_first_time", true)) {
			sharedPrefs.edit().putBoolean("my_first_time", false).commit()
			return true
		}
		return false
	}

	private fun checkloginStatus():String?{

		val prefsName = MyVariables.sharedPrefsName

		val sharedPrefs = getSharedPreferences(prefsName, 0)

		if (sharedPrefs.getBoolean("logged-in", true)) {
			return sharedPrefs.getString("access key", null)
		}
		return null
	}

	override fun onRestart() {
		super.onRestart()
		if(MyVariables.accessToken != null) loadHomepage()
	}

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

		if(requestCode==1 && resultCode === Activity.RESULT_OK){
			// finishing logging-in/registering
			setSupportActionBar(toolbar)
			supportActionBar?.title = "EZventory"

			loadHomepage()
		}

		if(requestCode==2 && resultCode === Activity.RESULT_OK){
			// Adding a new item
			loadHomepage()
		}

		if(requestCode==3 && resultCode === Activity.RESULT_OK){
			// options page
			val prefsName = MyVariables.sharedPrefsName
			val sharedPrefs = getSharedPreferences(prefsName, 0)

			if(!sharedPrefs.getBoolean("logged-in", false)){
				d("Dinu", "logged out")
				loadStartupPage()
			} else {
				d("Dinu", "HERE")
				loadHomepage()
			}
		}
    }

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

	private fun loadStartupPage(){
		intent = Intent(this, LandingPage::class.java)
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		startActivityForResult(intent, 1)
	}

	private fun loadHomepage() {
		fab.setOnClickListener {
			intent = Intent(this, AddProductActivity::class.java)
			startActivityForResult(intent, 2)
		}

		optionsButton.setOnClickListener {
			d("Dinu", "Options Clicked")
			intent = Intent(this, LoginOptionsPage::class.java)
			startActivityForResult(intent, 3)
		}

		val url = HttpUrl.Builder()
			.scheme(scheme)
			.host(host)
			.build()

		val request = Request.Builder()
			.url(url)
			.header("Authorization", "Bearer "+MyVariables.accessToken)
			.build()

		client.newCall(request).enqueue(object: Callback {

			override fun onResponse(call: Call, response: Response) {
				val body = response?.body?.string()
				val gson = GsonBuilder().create()

				val importedProducts = gson.fromJson(body, Array<Product>::class.java)

				MyVariables.productsList.products = importedProducts.toMutableList()
				MyVariables.filteredList.products = MyVariables.productsList.products

				names = setNames()
				locations = setLocations()

				this@MainActivity.runOnUiThread(Runnable {
					getPrice()

					sp = findViewById(R.id.filterByName)
					sp2 = findViewById(R.id.filterByPrice)

					addFilters()
				})

			}
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to Execute Request")
				println(e)
			}
		})

	}

    private fun applyFilters() {
        MyVariables.filteredList.products = locationButton(nameButton())

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
        //var prices = appConfig.prices.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, locations)
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
                locationSelected = locations[position]
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

		MyVariables.productsList.products.forEach {
			originalNames.add(it.event.toUpperCase())
		}

		return originalNames.toTypedArray()
    }

	private fun setLocations(): Array<String> {
		var originalLocations= appConfig.locations.toMutableSet()

		MyVariables.productsList.products.forEach {
			originalLocations.add(it.location.toUpperCase())
		}

		return originalLocations.toTypedArray()
	}

	private fun locationButton(productList: MutableList<Product>): MutableList<Product> {
		var newProducts = mutableListOf<Product>()

		if(locationSelected == "ALL") {
			return productList
		}

		productList.forEach {
			if (it.location.toUpperCase() == locationSelected) {
				newProducts.add(it)
			}
		}
		return newProducts
	}

    private fun nameButton(): MutableList<Product> {
        var newProducts = mutableListOf<Product>()
        if(eventSelected != "ALL") {
            MyVariables.productsList.products.toList().forEach {
                if (it.event.toUpperCase() == eventSelected) {
                    newProducts.add(it)
                }
            }
        }else {
            newProducts = MyVariables.productsList.products.toMutableList()
        }
        return newProducts
    }

}


