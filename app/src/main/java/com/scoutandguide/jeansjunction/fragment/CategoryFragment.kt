package com.scoutandguide.jeansjunction.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.scoutandguide.jeansjunction.activity.ProductDetailActivity
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.adapter.AdapterProduct
import com.scoutandguide.jeansjunction.databinding.FragmentCategoryBinding
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {

    private  lateinit var binding : FragmentCategoryBinding
    private val viewModel : UserViewModel by viewModels()
    private   var category :String?=null
    private lateinit var  adapterProduct: AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentCategoryBinding.inflate(layoutInflater)


        getProductCategory()
        setToolbarTitle()
        onNavigationIconClicked()
        fetchCategoryProduct()
        return binding.root
    }

    private fun getProductCategory() {
        val bundle = arguments
        category= bundle?.getString("category")

    }

    private fun setToolbarTitle() {
        binding.tbSearchFragment.title=category
    }

    private fun onNavigationIconClicked() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
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

        startActivity(Intent(requireContext(), ProductDetailActivity::class.java))


    }

    }



