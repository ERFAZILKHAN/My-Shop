package com.example.my_shop1.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.my_shop1.MainActivity
import com.example.my_shop1.activity.AddressActivity
import com.example.my_shop1.activity.CategoryActivity
import com.example.my_shop1.adapter.CartAdapter
import com.example.my_shop1.databinding.FragmentCartBinding
import com.example.my_shop1.roomdp.AppDatabase
import com.example.my_shop1.roomdp.ProductModel

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var list:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val isCart = preference.getBoolean("isCart", false)

        // Initialize database and observe product list changes
        val dao = AppDatabase.getInstance(requireContext()).productDao()

        list = ArrayList()

        dao.getAllProduct().observe(viewLifecycleOwner) { productList ->
            Log.d("CartFragment", "Product List Size: ${productList.size}")
            binding.cartRecycler.adapter = CartAdapter(requireContext(), productList)

            list.clear()
            for (data in productList ){
                list.add(data.productId)
            }

            totalCost(productList)
        }

        // Check isCart value and navigate to MainActivity if true
        if (isCart) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()


        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total = 0
        for (item in data!!){

            total += item.productSp!!.toInt()
        }

        binding.textView12.text = "Total Item In Cart is ${data.size}"
        binding.textView13.text = "Total Cost : ${total}"

        binding.checkout.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra("totalCost",total)
            intent.putExtra("productIds",list)
            startActivity(intent)

        }

    }
}