package com.scoutandguide.jeansjunction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.scoutandguide.jeansjunction.databinding.ActivityEachOrderDetailsActvityBinding

class EachOrderDetailsActvity : AppCompatActivity() {
    private lateinit var binding: ActivityEachOrderDetailsActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEachOrderDetailsActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get order details from intent
        val productTitle = intent.getStringExtra("productTitle")
        val productCategory = intent.getStringExtra("productCategory")
        val productPrice = intent.getStringExtra("productPrice")
        val productSize = intent.getStringExtra("productSize")
        val productCount = intent.getStringExtra("productCount")
        val productImage = intent.getStringExtra("productImage")
        val orderDate = intent.getStringExtra("orderDate")
        val status = intent.getIntExtra("productStatus", 0) // Correctly getting it as an Int

        // Set order details to views
        binding.tvProductTitle.text = productTitle
        binding.tvProductCategory.text = productCategory
        binding.tvProductPrice.text = "Price: ${productPrice}"
        binding.tvProductSize.text = "Size: ${productSize}"

        binding.tvProductCount.text = productCount

        productImage?.let {
            Glide.with(this)
                .load(it) // URL or resource ID
                .into(binding.ivProdudctImage)
        }

        // Update status views
        settingStatus(status)
    }

    private fun settingStatus(status: Int) {
        val statusToViews = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2),
            3 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2, binding.iv4, binding.view3)
        )

        val viewsToInt = statusToViews[status] ?: emptyList()

        // Reset the background color of all views to the default color before setting the new ones
        resetStatusViews()

        // Set the background color of the views according to the status
        for (view in viewsToInt) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }
    }

    private fun resetStatusViews() {
        val allViews = listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2, binding.iv4, binding.view3)
        for (view in allViews) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black) // Set your default color here
        }
    }
}