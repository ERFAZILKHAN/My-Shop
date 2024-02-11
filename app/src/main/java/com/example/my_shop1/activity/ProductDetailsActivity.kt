package com.example.my_shop1.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.my_shop1.MainActivity
import com.example.my_shop1.databinding.ActivityProductDetailsBinding
import com.example.my_shop1.roomdp.AppDatabase
import com.example.my_shop1.roomdp.ProductDao
import com.example.my_shop1.roomdp.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("id")
        getProductDetails(productId)
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener { documentSnapshot ->
                val list = documentSnapshot.get("productImage") as ArrayList<String>
                binding.textView7.text = documentSnapshot.getString("productName")
                binding.textView8.text = documentSnapshot.getString("productSp")
                binding.textView9.text = documentSnapshot.getString("productDescription")

                val slideList = ArrayList<SlideModel>()
                for (data in list) {
                    slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }
                val name = documentSnapshot.getString("productName")
                val productSp = documentSnapshot.getString("productSp")
                cartAction(proId, name, productSp, documentSnapshot.getString("productCoverImg"))
                binding.imageSlider.setImageList(slideList)

            }.addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val productDao = AppDatabase.getInstance(this).productDao()

        if (productDao.isExist(proId) != null) {
            binding.textView10.text = "Go To Cart"
        } else {
            binding.textView10.text = "Add To Cart"
        }

        binding.textView10.setOnClickListener {
            if (productDao.isExist(proId) != null) {
                openCart()
            } else {
                addToCart(productDao, proId, name, productSp, coverImg)
            }
        }
    }

    private fun addToCart(
        productDao: ProductDao,
        proId: String,
        name: String?,
        productSp: String?,
        coverImg: String?
    ) {
        val data = ProductModel(proId, name, coverImg, productSp)
        lifecycleScope.launch(Dispatchers.IO) {
            productDao.insertProduct(data)
            binding.textView10.text = "Go To Cart"
        }
    }

    private fun openCart() {
        val preference = getSharedPreferences("info", MODE_PRIVATE)
        val isCart = preference.getBoolean("isCart", false)

        if (isCart) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }
}


