package com.ikhokha.features.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.models.Product
import com.ikhokha.features.cart.databinding.FragmentCartBinding
import com.ikhokha.viewmodels.CartViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : TopNavigationFragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModel()
   // private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel.products.observe(this) { onProductsSet(it) }
        cartViewModel.productsError.observe(this) { onProductsError(it) }
        cartViewModel.deletedPosition.observe(this) { onProductsDeleted(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        binding.lifecycleOwner = this
        binding.cartViewModel = cartViewModel
        return binding.root
    }

    private fun onProductsSet(product: List<Product>) {

    }

    private fun onProductsError(errorMessage: String) {
        //Show error dialog
    }

    private fun onProductsDeleted(position: Int) {

    }
}