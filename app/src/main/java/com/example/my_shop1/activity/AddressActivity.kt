package com.example.my_shop1.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.my_shop1.R
import com.example.my_shop1.databinding.ActivityAddressBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var  preferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences= this.getSharedPreferences("user", MODE_PRIVATE)

        lordUserInfo()

        binding.proceed.setOnClickListener {
            validateData(
                binding.userName.text.toString(),
                binding.userNumber.text.toString(),
                binding.userPinCode.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userVillage.text.toString(),
            )
        }
    }



    private fun validateData(number: String, name: String, pinCode: String, city: String, state: String, village: String) {

        if (number.isEmpty() || state.isEmpty() || city.isEmpty() || name.isEmpty()||pinCode.isEmpty()||village.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }else{
            storeData(city,village,state,pinCode)
        }

    }
    private fun storeData( city: String, village: String, state: String, pinCode: String) {
        val map = hashMapOf<String,Any>()

        map["village"] = village
        map["state"] = state
        map["city"] = city
        map["pinCode"] = pinCode

        Firebase.firestore.collection("users")
            .document(preferences.getString("number" , "")!!)
            .update(map).addOnSuccessListener {
                val intent = Intent(this, CheckOutActivity::class.java)
                intent.putExtra("productIds",intent.getStringArrayExtra("productIds"))
                startActivity(intent)


            }.addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }
    }


    private fun lordUserInfo() {

        Firebase.firestore.collection("users")
            .document(preferences.getString("number" , "")!!)
            .get().addOnSuccessListener {

                binding.userName.setText(it.getString("userName"))
                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.userVillage.setText(it.getString("village"))
                binding.userState.setText(it.getString("state"))
                binding.userCity.setText(it.getString("city"))
                binding.userPinCode.setText(it.getString("pinCode"))
            }

    }
}