package com.example.my_shop1.roomdp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductModel)

    @Delete
    suspend fun deletProduct(product: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllProduct(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExist(id: String): ProductModel?
}