package com.example.my_shop1.adapter

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_shop1.activity.ProductDetailsActivity
import com.example.my_shop1.databinding.LayoutCartItemBinding
import com.example.my_shop1.roomdp.AppDatabase
import com.example.my_shop1.roomdp.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartAdapter( val context: Context,val list: List<ProductModel>) :
RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding : LayoutCartItemBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
       val binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Glide.with(context).load(list[position].productImage).into(holder.binding.imageView4)

        holder.binding.textView11.text = list[position].productName
        holder.binding.textView12.text = list[position].productSp

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ProductDetailsActivity::class.java)
            intent.putExtra("id",list[position].productId)
            context.startActivity(intent)

        }



        val dao = AppDatabase.getInstance(context).productDao()

        holder.binding.imageView5.setOnClickListener {
            GlobalScope.launch {
                Dispatchers.IO
                dao.deletProduct(
                    ProductModel(list[position].productId,
                    list[position].productName,
                    list[position].productImage,
                    list[position].productSp
                    )
                )
            }
        }

    }


}