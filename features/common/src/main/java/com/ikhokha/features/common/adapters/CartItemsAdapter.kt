package com.ikhokha.features.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ikhokha.common.models.Product
import com.ikhokha.features.common.R

class CartItemsAdapter(private val context: Context, private val products: List<Product>, val layout: Int) :
    RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {
    private var productListener: ProductListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvQuantity?.text = "${product.quantity}x"
        holder.tvDescription?.text = product.description
        holder.tvPrice?.text = "R${product.price}"
        holder.imgBtnDelete?.setOnClickListener {
            productListener?.onDeleteProductClicked(product, position)
        }
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        internal var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        internal var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        internal var imgBtnDelete: ImageButton = itemView.findViewById(R.id.imgBtnDelete)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val product = products[layoutPosition]
            productListener?.onProductClicked(product, layoutPosition)
        }
    }

    interface ProductListener {
        fun onProductClicked(product: Product, position: Int)
        fun onDeleteProductClicked(product: Product, position: Int)
    }

    fun addProductListener(productListener: ProductListener) {
        this.productListener = productListener
    }

    override fun getItemCount() = products.size

}