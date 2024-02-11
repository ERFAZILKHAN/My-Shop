package com.example.my_shop1.model

import androidx.room.Entity

data class AddProductModel(

    val productCategory:String? = "",
    val productCoverImg:String? = "",
    val productDescription:String? = "",
    val productId:String? = "",
    val productImage:ArrayList<String> = ArrayList(),
    val productMrp:String? = "",
    val productName:String? = "",
    val productSp:String? = ""
)
