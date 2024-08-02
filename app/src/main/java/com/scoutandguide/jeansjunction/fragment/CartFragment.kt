package com.scoutandguide.jeansjunction.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.scoutandguide.jeansjunction.activity.ProfileActivity
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.activity.MainActivity
import com.scoutandguide.jeansjunction.adapter.CartAdapter
import com.scoutandguide.jeansjunction.databinding.FragmentCartBinding
import com.scoutandguide.jeansjunction.model.CartProduct
import com.scoutandguide.jeansjunction.viewModel.UserViewModel

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: UserViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private val cartProducts = mutableListOf<CartProduct>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCart.layoutManager = LinearLayoutManager(context)
        cartAdapter = CartAdapter(cartProducts, ::deleteCartProduct)
        binding.rvCart.adapter = cartAdapter

        binding.btnPay.setOnClickListener {
            if (cartProducts.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                Utils.getCurrentUserId()?.let { userId ->
                    viewModel.checkUserProfileComplete(userId)
                    viewModel.isUserProfileComplete.observe(viewLifecycleOwner, Observer { isComplete ->
                        if (isComplete == true) {
                            handlePayment(userId)
                        } else if (isComplete == false) {
                            Toast.makeText(requireContext(), "Please complete your profile", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), ProfileActivity::class.java))

                        }
                    })
                }
            }
        }

        loadCartProducts()

        binding.tbProfileFragment.setNavigationOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
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
                    binding.tvTotalAmount.text = "Shipping Charge: ₹${50}"
                    binding.btnPay.text = "Pay Rs ${totalAmount + 50}"
                } else {
                    binding.tvTotalAmount.text = "Shipping Charge: ₹${0}"
                    binding.btnPay.text = "Pay Rs ${totalAmount}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun deleteCartProduct(cartProduct: CartProduct) {
        Log.d("RishabhCheck", cartProduct.userId.toString())
        viewModel.deleteProductFromCart(cartProduct)
    }

    private fun handlePayment(userId: String) {
        Utils.showDialog(requireContext(), "Order Placing")

        viewModel.saveOrder(userId, cartProducts) { isSuccess ->
            if (isSuccess) {
                // Clear the cart in the UI
                cartProducts.clear()
                cartAdapter.notifyDataSetChanged()

                // Navigate to MainActivity and show toast
                Toast.makeText(requireContext(), "Order placed Successfully", Toast.LENGTH_SHORT).show()
                Utils.hideDialog()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Failed to place order", Toast.LENGTH_SHORT).show()
                Utils.hideDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
