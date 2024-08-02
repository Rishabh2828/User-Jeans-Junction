// CartAdapter.kt
package com.scoutandguide.jeansjunction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scoutandguide.jeansjunction.databinding.ItemViewCartProductsBinding
import com.scoutandguide.jeansjunction.model.CartProduct

class CartAdapter(
    private val cartProducts: List<CartProduct>,
    private val onDeleteClick: (CartProduct) -> Unit // Add callback for delete action
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemViewCartProductsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemViewCartProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartProduct = cartProducts[position]
        with(holder.binding) {
            // Load product image using Glide
            Glide.with(ivProdudctImage.context)
                .load(cartProduct.productImage)
                .into(ivProdudctImage)

            tvProductTitle.text = cartProduct.productTitle
            tvProductCategory.text = cartProduct.productCategory
            tvProductPrice.text = "₹${cartProduct.price}"
            tvProductCount.text = cartProduct.productCount.toString()

            // Set up delete button click listener
            deleteProdut.setOnClickListener {
                onDeleteClick(cartProduct)
            }
        }
    }

    override fun getItemCount() = cartProducts.size
}
