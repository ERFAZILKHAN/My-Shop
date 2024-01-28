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
   private  var name:String? = null
    private  var productSp:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))

        setContentView(binding.root)

    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImage") as ArrayList<String>
                binding.textView7.text = it.getString("productName")
                binding.textView8.text = it.getString("productSp")
                binding.textView9.text = it.getString("productDescription")

                val slideList = ArrayList<SlideModel>()
                for(data in list){
                    slideList.add(SlideModel(data,ScaleTypes.CENTER_CROP))
                }


                cartAction(proId,name,productSp,it.getString("productCoverImg"))


                binding.imageSlider.setImageList(slideList)


            }.addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()

            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val productDao = AppDatabase.getInstance(this).productDao()

        if (productDao.isExit(proId) != null){
            binding.textView10.text = "Go To Cart"

        }else{
            binding.textView10.text = "Add To Cart"

        }
        binding.textView10.setOnClickListener {
            if (productDao.isExit(proId) != null){
                openCart()

            }else{
                addToCart(productDao,proId,name,productSp,coverImg)


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
        val data = ProductModel(proId,name,coverImg,productSp)  // issu yaha ho sakta ha 2:38 min

        lifecycleScope.launch(Dispatchers.IO) {
            productDao.insertProduct(data)
            binding.textView10.text = "Go To Cart"

        }
    }


    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart",true)
        editor.apply()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


}



