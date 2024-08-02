package com.scoutandguide.jeansjunction.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.scoutandguide.jeansjunction.adapter.CategoriesAdapter
import com.scoutandguide.jeansjunction.model.Category
import com.scoutandguide.jeansjunction.Constants
import com.scoutandguide.jeansjunction.activity.ProductDetailActivity
import com.scoutandguide.jeansjunction.activity.CategoryActivity
import com.scoutandguide.jeansjunction.activity.SearchActivity
import com.scoutandguide.jeansjunction.adapter.AdapterProduct
import com.scoutandguide.jeansjunction.databinding.FragmentHomeBinding
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    val viewModel  : UserViewModel by viewModels()

    private lateinit var binding : FragmentHomeBinding
    private  lateinit var adapterProduct: AdapterProduct


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentHomeBinding.inflate(layoutInflater)
        setCategories()
        getBestSellerProducts("yes")
        navigatingToSearchFragment()

        return binding.root
    }


    private fun setCategories() {

        val categoryList = ArrayList<Category>()

        for (i in 0 until  Constants.allProductCategoryIcon.size){
            categoryList.add(Category(Constants.allProductCategory[i], Constants.allProductCategoryIcon[i]))
        }

        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryIconClicked)


    }

    private fun onCategoryIconClicked(category: Category) {
        val intent = Intent(requireContext(), CategoryActivity::class.java).apply {
            putExtra("category", category.title) // Pass the category name as a string extra
        }
        startActivity(intent)
    }

    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {

            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
    }



    private fun getBestSellerProducts(category: Any?) {

        binding.shimmerViewContainer.visibility= View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchBestsellerProducts(category).collect{

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
                binding.shimmerViewContainer.visibility= View.GONE

            }
        }

    }

    private fun onProductClicked(product: Product){

        val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra("product", product)
        }
        startActivity(intent)

    }


}