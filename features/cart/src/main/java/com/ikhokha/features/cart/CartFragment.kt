package com.ikhokha.features.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.models.Product
import com.ikhokha.features.cart.databinding.FragmentCartBinding
import com.ikhokha.features.common.adapters.CartItemsAdapter
import com.ikhokha.viewmodels.CartViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : TopNavigationFragment(), CartItemsAdapter.ProductListener {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModel()
    private lateinit var cartItemsAdapter: CartItemsAdapter

    override fun onStart() {
        super.onStart()
        drawerController.showBottomNav()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel.products.observe(this) { onProductsSet(it) }
        cartViewModel.productsError.observe(this) { onProductsError(it) }
        cartViewModel.deletedPosition.observe(this) { onProductDeleted(it) }
        cartViewModel.productDeleteError.observe(this) { onProductDeleteError(it) }
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
        cartViewModel.showLoading.value = false
    }

    private fun onProductsError(errorMessage: String) {
        //Show error dialog
    }

    private fun onProductDeleted(position: Int) {

    }

    private fun onProductDeleteError(errorMessage: String) {

    }

    override fun onProductClicked(product: Product, position: Int) {
        Toast.makeText(
            requireContext(),
            "incremented",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDeleteProductClicked(product: Product, position: Int) {
        cartViewModel.deleteProduct(product.id, position)
    }
}