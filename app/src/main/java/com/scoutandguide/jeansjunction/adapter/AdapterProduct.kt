package com.scoutandguide.jeansjunction.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.scoutandguide.jeansjunction.FilteringProducts
import com.scoutandguide.jeansjunction.databinding.ItemViewProductBinding
import com.scoutandguide.jeansjunction.model.Product


class AdapterProduct(val onProductClicked: (Product) -> Unit) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable{
    class ProductViewHolder(val binding : ItemViewProductBinding) :ViewHolder(binding.root){

    }


    val diffUtil = object :DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return  ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
      return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = differ.currentList[position]




        holder.binding.apply {

            val imageList = ArrayList<SlideModel>()

            val productImage = product.productImageUris

            for (i in 0 until productImage?.size!!){
                imageList.add(SlideModel(product.productImageUris!![i].toString()))
            }

            ivImageSlider.setImageList(imageList)
            tvProductTitle.text= product.productTitle
            tvProductColor.text= product.productType
            tvProductPrice.text= "₹"+product.productPrice





        }

        holder.itemView.setOnClickListener {
            onProductClicked(product)
        }

        holder.binding.ivImageSlider.setOnClickListener {
            Log.d("AdapterProduct", "Image slider clicked")

            onProductClicked(product)

        }

      holder.binding.ivImageSlider.setItemClickListener(object : ItemClickListener {
          override fun doubleClick(position: Int) {
              TODO("Not yet implemented")
          }

          override fun onItemSelected(position: Int) {
              onProductClicked(product)
            }
        })




    }

    private val filter : FilteringProducts?= null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filter==null) return  FilteringProducts(this, originalList)
        return  filter
    }





}