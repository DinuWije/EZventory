package com.dinuw.firstapp

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Product (
    @PrimaryKey val itemID: Int,
    @ColumnInfo(name = "item_name") var name: String,
    @ColumnInfo(name = "school_event") var eventSchool: String,
    @ColumnInfo(name = "storage_location") var storage_location: String,
    @ColumnInfo(name = "quantity") var quantity: Int,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "image") var image: String?

) : Serializable
