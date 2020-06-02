package com.dinuw.firstapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*

var productslist = MainActivity.MyVariables.filteredList

class MainAdapter: RecyclerView.Adapter<CustomViewHolder>(){

    //number of Items

    override fun getItemCount(): Int {
        return productslist.products.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.item_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val name = productslist.products.get(position).name
        val eventSchool = "Event: ${productslist.products.get(position).eventSchool}"
        val itemLocation = "Location: ${productslist.products.get(position).storage_location}"
        val quantity = productslist.products.get(position).quantity.toString()
        val picLocation = productslist.products.get(position).image

        holder.view.recyclerItemName?.text = name
        holder.view.recyclerEventName?.text = eventSchool
        holder.view.recyclerLocation?.text = itemLocation
        holder.view.recyclerQuantity?.text = quantity
        if (picLocation != null){
            val takenImage = BitmapFactory.decodeFile(picLocation)
            holder.view.miniImage?.setImageBitmap(takenImage)
        }
    }


}



class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){
//    init{
//        view.setOnClickListener {
//            val intent = Intent(view.context, ViewItemActivity::class.java)
//            intent.putExtra("productslist", productslist)
//			MainActivity.startActivityForResult(intent, 1)
//        }
//    }

}
