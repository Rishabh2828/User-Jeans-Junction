package com.scoutandguide.jeansjunction.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.scoutandguide.jeansjunction.adapter.AdapterProduct
import com.scoutandguide.jeansjunction.databinding.ActivitySearchBinding
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    val viewModel  : UserViewModel by viewModels()

    private  lateinit var binding: ActivitySearchBinding
    private lateinit var  adapterProduct: AdapterProduct
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllTheProducts("All")
        searchProducts()
        backToHomeFragment()
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val query = s.toString().trim()
                adapterProduct.filter?.filter(query)

            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun getAllTheProducts(category: Any?) {

        binding.shimmerViewContainer.visibility= View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect{

                if (it.isEmpty()){
                    binding.rvProducts.visibility= View.GONE
                    binding.tvText.visibility= View.VISIBLE
                }else{
                    binding.rvProducts.visibility= View.VISIBLE
                    binding.tvText.visibility= View.GONE
                    Log.d("rishabh",it.size.toString())
                }

                adapterProduct = AdapterProduct(::onProductClicked)
                binding.rvProducts.adapter= adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList= it as ArrayList<Product>

                binding.shimmerViewContainer.visibility= View.GONE

            }
        }

    }

    private fun onProductClicked(product: Product){


        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("product", product)
        }
        startActivity(intent)


    }

    private fun backToHomeFragment() {
        binding.searchBackbtn.setOnClickListener {
            startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        }

    }

}