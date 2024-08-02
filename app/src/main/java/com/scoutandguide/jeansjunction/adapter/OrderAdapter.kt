package com.scoutandguide.jeansjunction.adapter

import android.R.attr
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.scoutandguide.jeansjunction.EachOrderDetailsActvity
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.activity.OrderDetailActivity
import com.scoutandguide.jeansjunction.databinding.ItemViewOrdersBinding
import com.scoutandguide.jeansjunction.model.Orders
import java.lang.String
import kotlin.Int
import kotlin.with


class OrderAdapter(
    val context: Context,
    private val orders: List<Orders>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemViewOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        with(holder.binding) {
            tvOrderDate.text = order.orderDate ?: "Date not available"
            tvOrderTitles.text = order.productTitle ?: "No title"
            tvOrderAmount.text = "₹${order.price ?: 0.0}"
            tvOrderStatus.text = "Ordered" // Update based on your order status logic




            when(order.itemStatus){
                0 ->{
                    tvOrderStatus.text = "Ordered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.AppColor )

                }  1 ->{

                tvOrderStatus.text = "Received"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue )

            }  2 ->{

                tvOrderStatus.text = "Dispatched"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green )

            }  3 ->{
                tvOrderStatus.text = "Delivered"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange )

            }
            }

            holder.itemView.setOnClickListener { v ->
                val intent =
                    Intent(context, EachOrderDetailsActvity::class.java)
                intent.putExtra("productTitle", order.productTitle)
                intent.putExtra("productCategory", order.productCategory)
                intent.putExtra("productImage", order.productImage)
                intent.putExtra("productPrice", String.valueOf(order.price))
                intent.putExtra("productStatus", order.itemStatus)
                intent.putExtra("productSize", order.sizeSelected)
                intent.putExtra(
                    "productCount",
                    String.valueOf(order.productCount)
                )
                context.startActivity(intent)
            }


        }
    }

    override fun getItemCount() = orders.size
}
