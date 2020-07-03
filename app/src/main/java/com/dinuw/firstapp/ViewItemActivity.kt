package com.dinuw.firstapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log.d
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.view_item.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.create
import java.io.File
import java.io.IOException
import kotlin.collections.set


class ViewItemActivity : AppCompatActivity() {

	private var productList2 = ProductsData()
	private var position = 0
	private var id : Int? = 0

	private lateinit var photoFile: File
	private var FILE_NAME = ("photo")
	private var takenImage: Bitmap? = null
	private var imageTaken = false

	//http request info
	private val scheme = "http"
	private val host = "10.0.2.2"
	private val port = 5000
	private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if(getIntent().getExtras() != null){
			productList2 = getIntent().getSerializableExtra("productsList") as ProductsData
			position = getIntent().getSerializableExtra("position") as Int
			id = productList2.products[position].id
		}

        setContentView(R.layout.view_item)

		setScreenText()

		deleteItem.setOnClickListener{
			deleteItem()
		}

		editItem.setOnClickListener {
			editItem()
		}

		backButton.setOnClickListener {
			goBack()
		}
    }

	private fun setScreenText(){
		var item = productList2.products[position]
		itemName.text = item.name
		itemEvent.text = "Event: ${item.event}"
		itemPrice.text = "$${MainActivity.MyFunctions.roundDouble(item.price)}"
		itemQuantity.text = "${item.quantity} in ${item.location}"
		val picLocation = item.image

		if (picLocation != null){
			val takenImage = BitmapFactory.decodeFile(picLocation)
			imageDisplay?.setImageBitmap(takenImage)
		}

	}

	private fun deleteItem(){

		val url = HttpUrl.Builder()
			.scheme(scheme)
			.host(host)
			.port(port)
			.addPathSegment("/delete_item")
			.addQueryParameter("id", id.toString())
			.build()

		val request = Request.Builder()
			.url(url)
			.delete()
			.build();

		client.newCall(request).enqueue(object: Callback {
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to Execute Request")
				println(e)
			}

			override fun onResponse(call: Call, response: Response) {
				println("Successfully Deleted")
				val body = response?.body?.string()
				println(body)
			}
		})

		finish()

	}

	private fun editItem(){
		var item = productList2.products[position]
		setContentView(R.layout.add_product)
		addItemText.text = "Edit Item"
		recyclerItemName.hint = "Current Name: ${item.name}"
		schoolEvent.hint = "Current Event: ${item.event}"
		quantity.hint = "Current Quantity: ${item.quantity}"
		priceOfItem.hint = "Current Total Value: $${MainActivity.MyFunctions.roundDouble(item.price)}"
		storageLocation.hint = "Current Storage Location: ${item.location}"

		val picLocation = item.image
		if (picLocation != null){
			val takenImage = BitmapFactory.decodeFile(picLocation)
			sampleImage?.setImageBitmap(takenImage)
		}

		optionalTakePhoto()

		productSubmitButton.setOnClickListener {
			val tempProductDict = mutableMapOf<String, Any?>("id" to id)
			if(recyclerItemName.text != null && recyclerItemName.text.toString() !=""){
				item.name = recyclerItemName.text.toString()
				tempProductDict["name"] = item.name
			}
			if(schoolEvent.text != null && schoolEvent.text.toString() !=""){
				item.event = schoolEvent.text.toString()
				tempProductDict["event"] = item.event
			}
			if(quantity.text != null && quantity.text.toString() !=""){
				item.quantity = quantity.text.toString().toInt()
				tempProductDict["quantity"] = item.quantity
			}
			if(priceOfItem.text != null && priceOfItem.text.toString() !=""){
				item.price = priceOfItem.text.toString().toDouble()
				tempProductDict["price"] = item.price
			}
			if(storageLocation.text != null && storageLocation.text.toString() !=""){
				item.location = storageLocation.text.toString()
				tempProductDict["location"] = item.location
			}
			if (imageTaken){
				item.image = photoFile.absolutePath
				tempProductDict["picture"] = item.image
			}



			val gson = GsonBuilder().create()
			val jsonProductList = gson.toJson(tempProductDict)

			val body = jsonProductList.toRequestBody("application/json".toMediaTypeOrNull())

			val url = HttpUrl.Builder()
				.scheme(scheme)
				.host(host)
				.port(port)
				.addPathSegment("/edit_item")
				.addQueryParameter("id", id.toString())
				.build()

			val request = Request.Builder()
				.put(body)
				.url(url)
				.build()

			client.newCall(request).enqueue(object: Callback {
				override fun onResponse(call: Call, response: Response) {
					println("Successfully Edited")
					val body = response?.body?.string()
					println(body)
				}
				override fun onFailure(call: Call, e: IOException) {
					println("Failed to Execute Request")
					println(e)
				}
			})

			finish()

		}

	}

	private fun optionalTakePhoto() {
		takePhoto.setOnClickListener {
			val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			photoFile = getPhotoFile(FILE_NAME)

			val fileProvider = FileProvider.getUriForFile(this, "com.dinuw.firstapp.fileprovider", photoFile)
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

			if (takePictureIntent.resolveActivity(this.packageManager) != null) {
				startActivityForResult(takePictureIntent, reqCode)
			} else {
				Toast.makeText(this, "Unable to Open Camera", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun goBack(){
		finish()
	}

	private fun getPhotoFile(fileName: String): File{
		val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		return File.createTempFile(fileName, ".jpg", storageDirectory)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == reqCode && resultCode == Activity.RESULT_OK){
			takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
			sampleImage.setImageBitmap(takenImage)
			imageTaken = true
		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}
}
