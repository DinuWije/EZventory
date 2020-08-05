package com.dinuw.firstapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.opening_screen.*
import kotlinx.android.synthetic.main.register.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LandingPage : AppCompatActivity() {


	//http request info
	private val scheme = "http"
	private val host = MainActivity.MyVariables.host
	private val client = OkHttpClient()

	private var status: String? = ""

	override fun onCreate(savedInstanceState: Bundle?){
		super.onCreate(savedInstanceState)
		setContentView(R.layout.opening_screen)

		register.setOnClickListener{
			setContentView(R.layout.register)
			register()
		}

		login.setOnClickListener {
			login()
		}
	}

	private fun register(){
		registerButton.setOnClickListener{
			val tempRegisterDict = mutableMapOf<String, Any?>()
			tempRegisterDict["username"] = checkNull(username.text.toString())
			val password = checkNull(password.text.toString())
			val passwordConfirmed = checkNull(passwordConfirmation.text.toString())
			if (password != passwordConfirmed){
				errorText.text = "Please make sure your passwords match"
			} else {
				tempRegisterDict["password"] = password
				httpRegisterRequest(tempRegisterDict)
			}
		}
	}

	private fun httpRegisterRequest(registerDict: Map<String, Any?>){
		val url = HttpUrl.Builder()
			.scheme(scheme)
			.host(host)
			.addPathSegment("/register")
			.build()

		val gson = GsonBuilder().create()
		val jsonRegisterDict = gson.toJson(registerDict)
		val body = jsonRegisterDict.toRequestBody("application/json".toMediaTypeOrNull())

		val request = Request.Builder()
			.url(url)
			.post(body)
			.build()

		client.newCall(request).enqueue(object: Callback {
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to Execute Request")
				println(e)
			}

			override fun onResponse(call: Call, response: Response) {
				status = response?.body?.string()
					if (status == "username taken") {
						this@LandingPage.runOnUiThread(Runnable {
							errorText.text = "Sorry, that username is already taken"
						})
						register()
					} else if (status != "error" && status != null) {
						MainActivity.MyVariables.accessToken = status
						confirmLogin(status)
						val resultIntent = Intent()
						setResult(Activity.RESULT_OK, resultIntent)
						finish()
					} else {
						errorText.text = status
					}
			}
		})
	}

	private fun login(){
		setContentView(R.layout.login)

		loginButton.setOnClickListener{
			val loginDict = mutableMapOf<String, Any?>()
			loginDict["username"] = checkNull(loginUsername.text.toString())
			loginDict["password"] = checkNull(loginPassword.text.toString())
			httpLoginRequest(loginDict)
		}
	}

	private fun httpLoginRequest(loginDict: Map<String, Any?>){
		val url = HttpUrl.Builder()
			.scheme(scheme)
			.host(host)
			.addPathSegment("/login")
			.build()

		val gson = GsonBuilder().create()
		val jsonProductList = gson.toJson(loginDict)
		val body = jsonProductList.toRequestBody("application/json".toMediaTypeOrNull())

		val request = Request.Builder()
			.post(body)
			.url(url)
			.build()

		client.newCall(request).enqueue(object: Callback {
			override fun onFailure(call: Call, e: IOException) {
				println("Failed to Execute Request")
				println(e)
			}

			override fun onResponse(call: Call, response: Response) {
				status = response?.body?.string()
				if (status != "error" && status != null) {
					MainActivity.MyVariables.accessToken = status
					confirmLogin(status)
					d("Dinu", "Access Token: " + MainActivity.MyVariables.accessToken)

					val resultIntent = Intent()
					setResult(Activity.RESULT_OK, resultIntent)

					finish()
				} else {
					this@LandingPage.runOnUiThread(Runnable {
						loginErrorText.text = "Incorrect username or password"
					})
				}
			}
		})
	}

	private fun checkNull(inputString: String): String {
		if(inputString != null && inputString !="") return inputString
		return "N/A"
	}

	private fun confirmLogin(accessKey:String?){
		val prefsName = MainActivity.MyVariables.sharedPrefsName

		val sharedPrefs = getSharedPreferences(prefsName, 0)

		sharedPrefs.edit().putString("access key", accessKey).commit()
		sharedPrefs.edit().putBoolean("logged-in", true).commit()
	}

	override fun onBackPressed() {

	}

}
