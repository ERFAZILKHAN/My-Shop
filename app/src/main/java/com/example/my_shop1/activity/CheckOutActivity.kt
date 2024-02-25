package com.example.my_shop1.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.my_shop1.MainActivity
import com.example.my_shop1.R
import com.example.my_shop1.roomdp.AppDatabase
import com.example.my_shop1.roomdp.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheckOutActivity : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        val checkout = Checkout()

        checkout.setKeyID("rzp_test_6ec3JTSsqt05dL");

        try {
            val options =  JSONObject()
            options.put("name", "MY Shop")
            options.put("description", "Best Ecommerce App")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#FFBB86FB")
            options.put("currency", "INR")
            options.put("amount", "50000") //pass amount in currency subunits
            options.put("prefill.email", "fazilrazakhan821@gmail.com")
            options.put("prefill.contact", "8218697721")
            checkout.open(this, options)

        } catch (e:Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        uploadData()
    }

    private fun uploadData() {
        val id = intent.getStringArrayListExtra("ProductIds")
        for(currentId in id!!){
            fetchData(currentId)

        }
    }

    private fun fetchData(productId: String?) {
        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener {
                lifecycleScope.launch(Dispatchers.IO){
                    dao.deletProduct(ProductModel(productId))
                }

                saveData(it.getString("ProductName"),
                    it.getString("productSp")
                    ,productId)
            }
    }

    private fun saveData(name:String?,price:String?,productId: String) {

        val preference = this.getSharedPreferences("user", MODE_PRIVATE)
        val data = hashMapOf<String,Any>()
        data["name"] = name!!
        data["price"] = price!!
        data["productId"] = productId
        data["status"] = "Ordered"
        data["userId"] = preference.getString("number","")!!

        val firestore = Firebase.firestore.collection("allOrdered")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.document(key).set(data).addOnSuccessListener {
            Toast.makeText(this, "Ordered Placed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }.addOnFailureListener {
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onPaymentError(errorCode: Int, errorMessage: String?) {
        Toast.makeText(this, "Payment Error: $errorMessage", Toast.LENGTH_SHORT).show()
        Log.e("CheckOutActivity", "Payment Error: Code $errorCode - $errorMessage")
        }

    override fun onDestroy() {
        super.onDestroy()

    }
}