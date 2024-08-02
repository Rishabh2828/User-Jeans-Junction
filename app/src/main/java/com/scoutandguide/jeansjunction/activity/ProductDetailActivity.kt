package com.scoutandguide.jeansjunction.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.adapter.AdapterSize
import com.scoutandguide.jeansjunction.databinding.ActivityProductDetailBinding
import com.scoutandguide.jeansjunction.model.CartProduct
import com.scoutandguide.jeansjunction.model.Product
import com.scoutandguide.jeansjunction.model.Size
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var adapterSize: AdapterSize
    private var productPrice: Double = 500.0
    private var productCount: Int = 1
    private val viewModel: UserViewModel by viewModels()
    private var selectedSize: Any? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getParcelableExtra<Product>("product")

        val imageList = ArrayList<SlideModel>()
        val RealImageList = ArrayList<SlideModel>()
        val productSizeList = ArrayList<Size>()

        product?.productImageUris?.forEach {
            it?.let { uri -> imageList.add(SlideModel(uri)) }
        }

        product?.productRealImageUris?.forEach {
            it?.let { uri -> RealImageList.add(SlideModel(uri)) }
        }

        product?.productMultipleSizes?.let {
            for (i in 0 until it.size - 1) {
                productSizeList.add(Size(it[i]))
            }
        }

        adapterSize = AdapterSize(productSizeList, ::getProductSize)
        binding.rvProductSize.adapter = adapterSize

        binding.ivImageSlider.setImageList(imageList)
        binding.ivImageSliderRealPics.setImageList(RealImageList)

        binding.apply {
            tvProductTitle.text = product?.productTitle
            productColor.text = product?.productColor
            productCategory.text = product?.productType
            productMaterial.text = product?.productFabric
            tvProductPrice.text = "₹${product?.productPrice?.times(productCount)}"
            toolbar.title = product?.productTitle
            productDetails.text = product?.productDetails
        }

        binding.tvIncrementCount.setOnClickListener {
            productCount++
            updateProductCountAndPrice()
        }

        binding.tvDecrementCount.setOnClickListener {
            if (productCount > 1) {
                productCount--
                updateProductCountAndPrice()
            }
        }

       // binding.btnAddToCart.text = "Add to cart"

        checkProductInCart(product)

        binding.btnAddToCart.setOnClickListener {
            if(binding.btnAddToCart.text =="Add to cart"){

                val productTitle = product?.productTitle
                val priceString = binding.tvProductPrice.text.toString()
                val cleanPriceString = priceString.replace("₹", "").trim()
                val price = cleanPriceString.toDoubleOrNull() ?: 0.0

                val userId = Utils.getCurrentUserId()
                val productRandomId = product?.productRandomId
                val productImage = product?.productImageUris?.firstOrNull()
                val productCount = binding.tvProductCount.text.toString().toIntOrNull() ?: 1
                val productCategory = product?.productType.toString()

                if (selectedSize == null && productSizeList.isNotEmpty()) {
                    selectedSize = productSizeList[0].size  // Default to the first size if none selected
                }

                val cartProduct = CartProduct(
                    productTitle = productTitle,
                    price = price,
                    productCount = productCount,
                    productRandomId = productRandomId,
                    productImage = productImage,
                    productCategory = productCategory,
                    userId = userId,
                    sizeSelected = selectedSize.toString()
                )

                viewModel.saveProduct(cartProduct)

                lifecycleScope.launch {
                    viewModel.isProductSaved.collect {
                        if (it) {
                            Utils.showToast(this@ProductDetailActivity, "Added to cart")
                            binding.btnAddToCart.text = "Go to cart"
                            binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(this@ProductDetailActivity, R.color.blue))

                        }
                    }
                }

            }else if (binding.btnAddToCart.text == "Go to cart") {
                val intent = Intent(this, CartActivity2::class.java)
                startActivity(intent)
            }

        }

    }

    private fun updateProductCountAndPrice() {
        binding.tvProductCount.text = productCount.toString()
        binding.tvProductPrice.text = "₹${productPrice * productCount}"
    }

    fun getProductSize(size: Any?) {
        if (size != null) {
            selectedSize = size
        }
    }

    private fun checkProductInCart(product: Product?) {
        val userId = Utils.getCurrentUserId()
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart Products/Users/$userId")

        cartRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartProductSnapshot in snapshot.children) {
                    val cartProduct = cartProductSnapshot.getValue(CartProduct::class.java)
                    if (cartProduct != null && cartProduct.productRandomId == product?.productRandomId) {
                        binding.btnAddToCart.text = "Go to cart"
                        binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(this@ProductDetailActivity, R.color.blue))
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
