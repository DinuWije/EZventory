package com.dinuw.firstapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log.d
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.add_product.*
import java.io.File
import java.io.Serializable

var itemID = 7
val reqCode = 69
private lateinit var photoFile: File
private var FILE_NAME = ("photo")


class AddProductActivity: AppCompatActivity(), Serializable {

    private var takenImage: Bitmap? = null
    private  var imageTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)
        var tempProductList = ProductsData()
        if(getIntent().getExtras() != null){
            tempProductList = getIntent().getSerializableExtra("productsData") as ProductsData
        }

        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        itemID = preferences.getInt("ItemID", 0)


        takePhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(this, "com.dinuw.firstapp.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(this.packageManager) != null){
                startActivityForResult(takePictureIntent, reqCode)
            } else {
               Toast.makeText(this, "Unable to Open Camera", Toast.LENGTH_SHORT).show()
            }
        }

        productSubmitButton.setOnClickListener {

            if(imageTaken){
                val tempProduct = Product(itemID, checkNull(recyclerItemName.text.toString()), checkNull(schoolEvent.text.toString()), checkNull(storageLocation.text.toString()), checkNullInt(quantity.text.toString()).toInt(), checkNullInt(priceOfItem.text.toString()).toDouble(), photoFile.absolutePath)
                tempProductList.products.add(tempProduct)
            } else {
                val tempProduct = Product(itemID, checkNull(recyclerItemName.text.toString()), checkNull(schoolEvent.text.toString()), checkNull(storageLocation.text.toString()), checkNullInt(quantity.text.toString()).toInt(), checkNullInt(priceOfItem.text.toString()).toDouble(), null)
                tempProductList.products.add(tempProduct)
            }
            itemID++

            val database = getSharedPreferences("database", Context.MODE_PRIVATE)
            database.edit().apply{
                putInt("ItemID", itemID)
            }.apply()

            val resultIntent = getIntent()
            resultIntent.putExtra("productsData", tempProductList)
            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }
    }

	private fun checkNull(inputString: String): String {
		if(inputString != null && inputString !=""){
			return inputString
		} else return "N/A"
	}

	private fun checkNullInt(inputString: String): String {
		if(inputString != null && inputString !=""){
			return inputString
		} else return "0"
	}

    private fun getPhotoFile(fileName: String): File{
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == reqCode && resultCode == Activity.RESULT_OK){
            takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            sampleImage.setImageBitmap(takenImage)
            d("Dinu", "${photoFile.absolutePath}")
            imageTaken = true
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
