package com.dinuw.firstapp

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Product (
    @PrimaryKey val itemID: Int,
    @ColumnInfo(name = "item_name") val name: String,
    @ColumnInfo(name = "school_event") val eventSchool: String,
    @ColumnInfo(name = "storage_location") val storage_location: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "image") var image: String?

) : Serializable