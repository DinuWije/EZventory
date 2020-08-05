package com.dinuw.firstapp

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Product (
	@PrimaryKey val id: Int?,
	@SerializedName("name") var name: String,
	@SerializedName("event") var event: String,
	@SerializedName("location") var location: String,
	@SerializedName("quantity") var quantity: Int,
	@SerializedName("price") var price: Double,
	@SerializedName("picture") var image: String?

) : Serializable
