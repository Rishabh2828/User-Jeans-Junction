package com.scoutandguide.jeansjunction.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scoutandguide.jeansjunction.databinding.ItemviewProductSizeBinding
import com.scoutandguide.jeansjunction.model.Size

class AdapterSize(private val sizeList: List<Size>, var selectedSize: (Any?) -> Unit) : RecyclerView.Adapter<AdapterSize.SizeViewHolder>() {

    private var selectedPosition = -1

    init {
        // Initialize the first item as selected if the list is not empty
        if (sizeList.isNotEmpty()) {
            selectedPosition = 0
            selectedSize(sizeList[0].size)  // Set the first size as the default selected size

        }
    }

    class SizeViewHolder(val binding: ItemviewProductSizeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = ItemviewProductSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = sizeList[position]
        holder.binding.apply {
            tvProductSize.text = size.size

            // Set background color and text color based on selection
            if (selectedPosition == position) {
                cvProduct.setCardBackgroundColor(Color.BLUE)
                tvProductSize.setTextColor(Color.WHITE)
            } else {
                cvProduct.setCardBackgroundColor(Color.TRANSPARENT)
                tvProductSize.setTextColor(Color.BLACK)
            }

            // Set click listener to update selected position
            cvProduct.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)

                selectedSize(size.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }
}
