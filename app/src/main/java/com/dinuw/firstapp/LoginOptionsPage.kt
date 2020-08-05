package com.dinuw.firstapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.options_page.*
import okhttp3.*
import java.io.File
import java.io.FileWriter
import com.opencsv.bean.StatefulBeanToCsv
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

private const val reqCode = 20
private var FILE_NAME = ("inventory")
private lateinit var inventoryFile: File

class LoginOptionsPage : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?){
		super.onCreate(savedInstanceState)
		setContentView(R.layout.options_page)

		logout.setOnClickListener {
			logout()
		}

		exportData.setOnClickListener {
			exportData(it)
		}

		backButton2.setOnClickListener {
			back()
		}
	}

	private fun logout(){
		val prefsName = MainActivity.MyVariables.sharedPrefsName

		val sharedPrefs = getSharedPreferences(prefsName, 0)

		sharedPrefs.edit().putString("access key", null).commit()
		sharedPrefs.edit().putBoolean("logged-in", false).commit()
		MainActivity.MyVariables.accessToken = null
		d("Dinu", "Logging out")
		back()
	}

	private fun exportData(view:View){
		try{
			val fileIntent = Intent(Intent.ACTION_SEND)
			inventoryFile = getInventoryFile(FILE_NAME)

			val fileProvider: Uri = FileProvider.getUriForFile(this, "com.dinuw.firstapp.fileprovider", inventoryFile)
			fileIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

			fileIntent.type = "text/csv"
			fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Inventory Data")
			fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			fileIntent.putExtra(Intent.EXTRA_STREAM, fileProvider)
			startActivityForResult(Intent.createChooser(fileIntent, "Send Inventory Data"), reqCode)
		}catch(e:Exception){
			e.printStackTrace()
		}
	}

	private fun back(){
		val resultIntent = Intent()
		setResult(Activity.RESULT_OK, resultIntent)
		finish()
	}

	private fun getInventoryFile(fileName: String): File{
		val products = MainActivity.MyVariables.productsList.products
		val data = StringBuilder()
		data.append("Name,Event,Location,Quantity,Price")
		products.forEach{
			data.append("\n"+it.name+","+it.event+","+it.location+","+it.quantity.toString()+","+it.price.toString())
		}

		val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		val file = File.createTempFile(fileName, ".csv", storageDirectory)
		file.writeText(data.toString())
		return file
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == reqCode && resultCode == Activity.RESULT_OK) {
			optionsText.text="Export Successful!"
		}
		else optionsText.text= "Couldn't export, please try again later."
	}

}
