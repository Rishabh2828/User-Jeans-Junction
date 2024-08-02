package com.scoutandguide.jeansjunction.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.adapter.OrderIdAdapter
import com.scoutandguide.jeansjunction.databinding.FragmentOrdersBinding
import com.scoutandguide.jeansjunction.model.OrderId
import com.scoutandguide.jeansjunction.viewModel.UserViewModel
import com.scoutandguide.jeansjunction.activity.OrderDetailActivity
import com.scoutandguide.jeansjunction.model.Orders

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var orderIdAdapter: OrderIdAdapter
    private val orderIds = mutableListOf<OrderId>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        binding.rvOrders.layoutManager = layoutManager
        orderIdAdapter = OrderIdAdapter(requireContext(), orderIds) { orderId ->
            // Handle order click
            val intent = Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("ORDER_ID", orderId)
            }
            startActivity(intent)
        }
        binding.rvOrders.adapter = orderIdAdapter

        loadOrders()
    }

    private fun loadOrders() {
        val userId = Utils.getCurrentUserId()
        val ordersRef = FirebaseDatabase.getInstance().getReference("Admins/Orders/$userId").orderByChild("orderTimestamp")

        Log.d("OrderCheck", "Loading orders for user: $userId")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("OrderCheck", "Data snapshot received")
                orderIds.clear()
                for (orderSnapshot in snapshot.children) {
                    val orderId = orderSnapshot.key



                    if (orderId != null) {
                        val firstOrderChild = orderSnapshot.children.firstOrNull()
                        if (firstOrderChild != null){
                            val firstOrder = firstOrderChild.getValue(Orders::class.java)
                            if (firstOrder != null) {
                                Log.d("Orderdate", "Order Date added: ${firstOrder.orderDate}")
                                Log.d("Orderdate", "Order Date added: ${firstOrder.itemStatus}")
                                orderIds.add(OrderId(orderId,firstOrder.orderDate,
                                    firstOrder.itemStatus.toString()
                                ))


                            }

                        }
                        Log.d("OrderCheck", "Order ID added: $orderId")
                        Log.d("Orderdate", "Order Date added: ")
                    } else {
                        Log.d("OrderCheck", "Order ID is null")
                    }
                }

                orderIdAdapter.notifyDataSetChanged()
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
                binding.rvOrders.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("OrderCheck", "Database error: ${error.message}")
                // Handle error
            }
        })
    }
}
