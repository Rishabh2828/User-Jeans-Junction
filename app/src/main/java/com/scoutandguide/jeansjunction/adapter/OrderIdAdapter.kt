package com.scoutandguide.jeansjunction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.databinding.ItemOrderIdBinding
import com.scoutandguide.jeansjunction.model.OrderId

class OrderIdAdapter(
    private val context: Context,
    private val orderIds: List<OrderId>,
    private val onOrderClick: (String) -> Unit
) : RecyclerView.Adapter<OrderIdAdapter.OrderIdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderIdViewHolder {
        val binding = ItemOrderIdBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderIdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderIdViewHolder, position: Int) {
        val orderId = orderIds[position].id
        val date = orderIds[position].orderDate
        val status = orderIds[position].itemStatus
        holder.binding.tvOrderDate.text = date
        holder.binding.root.setOnClickListener {
            onOrderClick(orderId)
        }

        holder.binding.apply {
            when(status){
                0.toString() ->{
                    tvOrderStatus.text = "Ordered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.AppColor )

                }  1.toString() ->{

                tvOrderStatus.text = "Received"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue )

            }  2.toString() ->{

                tvOrderStatus.text = "Dispatched"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green )

            }  3.toString() ->{
                tvOrderStatus.text = "Delivered"
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange )

            }
            }

        }

    }

    override fun getItemCount(): Int {
        return orderIds.size
    }

    class OrderIdViewHolder(val binding: ItemOrderIdBinding) : RecyclerView.ViewHolder(binding.root)
}
