package com.dinuw.firstapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log.d
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.add_product.view.*
import kotlinx.android.synthetic.main.item_row.view.*
import kotlinx.android.synthetic.main.view_item.*
import java.io.File

class ViewItemActivity : AppCompatActivity() {

	var productList2 = ProductsData()
	var position = 0

	private lateinit var photoFile: File
	private var FILE_NAME = ("photo")
	private var takenImage: Bitmap? = null
	private  var imageTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if(getIntent().getExtras() != null){
			productList2 = getIntent().getSerializableExtra("productsList") as ProductsData
			position = getIntent().getSerializableExtra("position") as Int
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
		var item = productList2.products.get(position)
		itemName.text = item.name
		itemEvent.text = "Event: ${item.eventSchool}"
		itemPrice.text = "$${MainActivity.MyFunctions.roundDouble(item.price)}"
		itemQuantity.text = "${item.quantity} in ${item.storage_location}"
		val picLocation = item.image

		if (picLocation != null){
			val takenImage = BitmapFactory.decodeFile(picLocation)
			imageDisplay?.setImageBitmap(takenImage)
		}

	}

	private fun deleteItem(){
		productList2.products.removeAt(position)
		MainActivity.MyVariables.productsD = productList2

		finish()
	}

	private fun editItem(){
		var item = productList2.products.get(position)
		setContentView(R.layout.add_product)
		addItemText.text = "Edit Item"
		recyclerItemName.hint = "Current Name: ${item.name}"
		schoolEvent.hint = "Current Event: ${item.eventSchool}"
		quantity.hint = "Current Quantity: ${item.quantity}"
		priceOfItem.hint = "Current Total Value: $${MainActivity.MyFunctions.roundDouble(item.price)}"
		storageLocation.hint = "Current Storage Location: ${item.storage_location}"
		val picLocation = item.image
		if (picLocation != null){
			val takenImage = BitmapFactory.decodeFile(picLocation)
			sampleImage?.setImageBitmap(takenImage)
		}

		optionalTakePhoto()

		productSubmitButton.setOnClickListener {
			if(recyclerItemName.text != null && recyclerItemName.text.toString() !=""){
				item.name = recyclerItemName.text.toString()
			}
			if(schoolEvent.text != null && schoolEvent.text.toString() !=""){
				item.eventSchool = schoolEvent.text.toString()
			}
			if(quantity.text != null && quantity.text.toString() !=""){
				item.quantity = quantity.text.toString().toInt()
			}
			if(priceOfItem.text != null && priceOfItem.text.toString() !=""){
				item.price = priceOfItem.text.toString().toDouble()
			}
			if(storageLocation.text != null && storageLocation.text.toString() !=""){
				item.storage_location = storageLocation.text.toString()
			}
			if (imageTaken){
				item.image = photoFile.absolutePath
			}

			MainActivity.MyVariables.productsD = productList2
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
