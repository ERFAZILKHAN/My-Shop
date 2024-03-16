package com.example.my_shop1.activity

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.firestore.FirebaseFirestore
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
        checkout.setKeyID("rzp_test_6ec3JTSsqt05dL")

        val price = intent.getStringExtra("totalCost")

        try {
            val options =  JSONObject()
            options.put("name", "MY Shop")
            options.put("description", "Best Ecommerce App")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#FFBB86FB")
            options.put("currency", "INR")
            options.put("amount", (price!!.toInt() * 100)) // pass amount in currency subunits
            options.put("prefill.email", "fazilrazakhan821@gmail.com")
            options.put("prefill.contact", "8218697721")
            checkout.open(this, options)
        } catch (e: Exception) {
            handlePaymentError("Something went wrong: ${e.message}")
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        try {
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
            uploadData()
        } catch (e: Exception) {
            handlePaymentError("An error occurred during payment success handling: ${e.message}")
        }
    }

    private fun uploadData() {
        val id = intent.getStringArrayListExtra("ProductIds")
        id?.forEach { productId ->
            fetchData(productId)
        }
    }

    private fun fetchData(productId: String?) {
        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener { documentSnapshot ->
                val productName = documentSnapshot.getString("ProductName")
                val productSp = documentSnapshot.getString("productSp")

                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deletProduct(ProductModel(productId))
                }

                saveData(productName, productSp, productId)
            }.addOnFailureListener { e ->
                handleFirestoreError("Failed to fetch data for product ID $productId: ${e.message}")
            }
    }

    private fun saveData(name: String?, price: String?, productId: String) {
        try {
            val preference = this.getSharedPreferences("user", MODE_PRIVATE)
            val data = hashMapOf<String, Any>()
            data["name"] = name!!
            data["price"] = price!!
            data["productId"] = productId
            data["status"] = "Ordered"
            data["userId"] = preference.getString("number", "")!!

            val firestore = FirebaseFirestore.getInstance().collection("allOrdered")
            val key = firestore.document().id
            data["orderId"] = key

            firestore.document(key).set(data).addOnSuccessListener {
                Toast.makeText(this, "Ordered Placed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                handleFirestoreError("Failed to save order data: ${e.message}")
            }
        } catch (e: Exception) {
            handlePaymentError("An error occurred: ${e.message}")
        }
    }

    override fun onPaymentError(errorCode: Int, errorMessage: String?) {
        val errorMsg = "Payment Error: Code $errorCode - $errorMessage"
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        Log.e("CheckOutActivity", errorMsg)
        handlePaymentError(errorMsg)
    }

    private fun handlePaymentError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("CheckOutActivity", message)
    }

    private fun handleFirestoreError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("CheckOutActivity", message)
        }
}
