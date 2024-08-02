package com.scoutandguide.jeansjunction.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.adapter.AdapterProduct
import com.scoutandguide.jeansjunction.databinding.ActivityCategoryBinding
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityCategoryBinding
    private val viewModel : UserViewModel by viewModels()
    private   var category :String?=null
    private lateinit var  adapterProduct: AdapterProduct
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        getProductCategory()
        setToolbarTitle()
        onNavigationIconClicked()
        onSearchMenuClicked()
        fetchCategoryProduct()
        onFilterClicked()
    }

    private fun getProductCategory() {
        category = intent.getStringExtra("category")

    }

    private fun setToolbarTitle() {
        binding.tbSearchFragment.title=category
    }

    private fun onNavigationIconClicked() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            startActivity(Intent(this@CategoryActivity, MainActivity::class.java))

        }
    }


    private fun onSearchMenuClicked() {
        binding.tbSearchFragment.setOnMenuItemClickListener {menuItem->
            when(menuItem.itemId){
                R.id.searchMenu ->{
                    startActivity(Intent(this@CategoryActivity, SearchActivity::class.java))
                    true
                }
                else -> {false}
            }

        }
    }

    private fun fetchCategoryProduct() {
        binding.shimmerViewContainer.visibility= View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category!!).collect{


                if (it.isEmpty()){
                    binding.rvProducts.visibility= View.GONE
                    binding.tvText.visibility= View.VISIBLE
                }else{
                    binding.rvProducts.visibility= View.VISIBLE
                    binding.tvText.visibility= View.GONE
                }


                adapterProduct = AdapterProduct(::onProductClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
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

    private fun onFilterClicked() {
        binding.filterLayout.setOnClickListener {
            showFilterDialog()
        }
    }


    private fun showFilterDialog() {
        val allProductType = arrayOf(
            "Slim Fit", "Regular Fit", "Skinny Fit", "Bootcut", "Straight Leg",
            "Relaxed Fit", "Skinny Jeans", "High-Waisted Jeans", "Bootcut Jeans",
            "Boyfriend Jeans", "Mom Jeans", "Straight Leg Jeans", "Boys' Jeans",
            "Girls' Jeans", "Toddler Jeans", "Baby Jeans", "Classic Jeans",
            "Ripped Jeans", "Distressed Jeans", "Faded Jeans", "Colored Jeans",
            "Printed Jeans", "Casual Jeans", "Party Jeans", "Office Wear Jeans",
            "Outdoor Jeans", "Travel Jeans"
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val btnRemoveFilter = dialogView.findViewById<Button>(R.id.btnRemoveFilter)
        val filterListView = dialogView.findViewById<ListView>(R.id.filterListView)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Select Filter")

        val alertDialog = builder.create()

        // Set up the list view with filter options
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, allProductType)
        filterListView.adapter = adapter
        filterListView.setOnItemClickListener { parent, view, position, id ->
            val selectedFilter = allProductType[position]
            lifecycleScope.launch {
                viewModel.fetchCategoryProducts(category, selectedFilter).collect {
                    if (it.isEmpty()) {
                        binding.rvProducts.visibility = View.GONE
                        binding.tvText.visibility = View.VISIBLE
                    } else {
                        binding.rvProducts.visibility = View.VISIBLE
                        binding.tvText.visibility = View.GONE
                    }

                    adapterProduct = AdapterProduct(::onProductClicked)
                    binding.rvProducts.adapter = adapterProduct
                    adapterProduct.differ.submitList(it)
                    alertDialog.dismiss()
                }
            }
        }

        // Set up the remove filter button
        btnRemoveFilter.setOnClickListener {
            fetchCategoryProduct()  // Call the method to fetch all products without filter
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}