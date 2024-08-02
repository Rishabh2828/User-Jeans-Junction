package com.scoutandguide.jeansjunction.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.adapter.CartAdapter
import com.scoutandguide.jeansjunction.databinding.ActivityCart2Binding
import com.scoutandguide.jeansjunction.model.CartProduct
import com.scoutandguide.jeansjunction.viewModel.UserViewModel

class CartActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityCart2Binding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private val cartProducts = mutableListOf<CartProduct>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCart2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initialize RecyclerView
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(cartProducts, ::deleteCartProduct)
        binding.rvCart.adapter = cartAdapter

        binding.btnPay.setOnClickListener {
            if (cartProducts.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                Utils.getCurrentUserId()?.let { userId ->
                    viewModel.checkUserProfileComplete(userId)
                    viewModel.isUserProfileComplete.observe(this, { isComplete ->
                        if (isComplete == true) {
                            handlePayment(userId)
                        } else if (isComplete == false) {
                            Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ProfileActivity::class.java))
                        }
                    })
                }
            }
        }

        loadCartProducts()

        binding.tbProfileFragment.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loadCartProducts() {
        val userId = Utils.getCurrentUserId()
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart Products/Users/$userId")

        cartRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartProducts.clear()
                var totalAmount = 0.0
                for (cartProductSnapshot in snapshot.children) {
                    val cartProduct = cartProductSnapshot.getValue(CartProduct::class.java)
                    if (cartProduct != null) {
                        cartProducts.add(cartProduct)
                        totalAmount += cartProduct.price ?: 0.0
                    }
                }
                cartAdapter.notifyDataSetChanged()
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE

                if (totalAmount < 500 && totalAmount > 0) {
                    binding.tvTotalAmount.visibility = View.VISIBLE
                    binding.tvTotalAmount.text = "Shipping Charge: ₹50"
                    binding.btnPay.text = "Pay Rs ${totalAmount + 50}"
                } else {
                    binding.tvTotalAmount.text = "Shipping Charge: ₹0"
                    binding.btnPay.text = "Pay Rs $totalAmount"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun deleteCartProduct(cartProduct: CartProduct) {
        Log.d("CartActivity", "Deleting product: ${cartProduct.userId}")
        viewModel.deleteProductFromCart(cartProduct)
    }

    private fun handlePayment(userId: String) {
        Utils.showDialog(this, "Order Placing")

        viewModel.saveOrder(userId, cartProducts) { isSuccess ->
            if (isSuccess) {
                // Clear the cart in the UI
                cartProducts.clear()
                cartAdapter.notifyDataSetChanged()

                // Navigate to MainActivity and show toast
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                Utils.hideDialog()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
                Utils.hideDialog()
            }
        }
    }


}