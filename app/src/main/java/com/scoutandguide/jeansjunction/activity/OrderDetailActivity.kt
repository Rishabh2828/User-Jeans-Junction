package com.scoutandguide.jeansjunction.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.adapter.OrderAdapter
import com.scoutandguide.jeansjunction.databinding.ActivityOrderDetailBinding
import com.scoutandguide.jeansjunction.model.Orders

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Orders>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvOrderDetails.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(this, orders)
        binding.rvOrderDetails.adapter = orderAdapter

        val userId = Utils.getCurrentUserId()
        val orderId = intent.getStringExtra("ORDER_ID") ?: return

        if (userId != null) {
            loadOrderDetails(userId, orderId)
        }
    }

    private fun loadOrderDetails(userId: String, orderId: String) {
        val ordersRef = FirebaseDatabase.getInstance().getReference("Admins/Orders/$userId/$orderId")

        Log.d("OrderDetail", "Loading order details for order ID: $orderId")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("OrderDetail", "Data snapshot received")
                orders.clear()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Orders::class.java)
                    if (order != null) {
                        orders.add(order)
                        Log.d("OrderDetail", "Order product added: ${order.productTitle}")
                    } else {
                        Log.d("OrderDetail", "Order product is null")
                    }
                }

                orderAdapter.notifyDataSetChanged()
             //   binding.shimmerViewContainer.stopShimmer()
             //   binding.shimmerViewContainer.visibility = View.GONE
            //    binding.rvOrderDetails.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("OrderDetail", "Database error: ${error.message}")
                // Handle error
            }
        })
    }
}
