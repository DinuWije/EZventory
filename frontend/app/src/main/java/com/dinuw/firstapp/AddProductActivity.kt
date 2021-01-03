package com.dinuw.firstapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.add_product.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.Exception

private const val reqCode = 10
private lateinit var photoFile: File
private var FILE_NAME = ("photo")

//http request info
private val scheme = "http"
private val host = MainActivity.MyVariables.host
private val client = OkHttpClient()


class AddProductActivity: AppCompatActivity(), Serializable {

    private var takenImage: Bitmap? = null
    private  var imageTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

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
			val tempProductDict = mutableMapOf<String, Any?>()
			tempProductDict["name"] = checkNull(recyclerItemName.text.toString())
			tempProductDict["event"] = checkNull(schoolEvent.text.toString())
			tempProductDict["location"] = checkNull(storageLocation.text.toString())
			tempProductDict["quantity"] = checkNullInt(quantity.text.toString()).toInt()
			tempProductDict["price"] = checkNullInt(priceOfItem.text.toString()).toDouble()

			try {
				tempProductDict["pic_location"] = photoFile.absolutePath.toString()
			} catch(e: Exception) {
					Log.e("exception", e.toString())
			}

			val gson = GsonBuilder().create()
			val jsonProductList = gson.toJson(tempProductDict)

			val body = jsonProductList.toRequestBody("application/json".toMediaTypeOrNull())

			val url = HttpUrl.Builder()
				.scheme(scheme)
				.host(host)
				.addPathSegment("/add_item")
				.build()

			val request = Request.Builder()
				.post(body)
				.url(url)
				.header("Authorization", "Bearer "+ MainActivity.MyVariables.accessToken)
				.build()

			client.newCall(request).enqueue(object: Callback {
				override fun onResponse(call: Call, response: Response) {
					val body = response?.body?.string()
					println(body)

					val resultIntent = Intent()
					setResult(Activity.RESULT_OK, resultIntent)

					finish()
				}
				override fun onFailure(call: Call, e: IOException) {
					println("Failed to Execute Request")
					println(e)
				}
			})

        }
    }

	private fun checkNull(inputString: String): String {
		if(inputString != null && inputString !=""){
			return inputString
		}
		return "N/A"
	}

	private fun checkNullInt(inputString: String): String {
		return if(inputString != null && inputString !=""){
			inputString
		} else "0"
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
