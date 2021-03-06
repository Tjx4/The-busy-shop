package com.ikhokha.features.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.extensions.runWhenReady
import com.ikhokha.common.helpers.showConfirmDialog
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.cart.databinding.FragmentCartBinding
import com.ikhokha.features.common.adapters.CartItemsAdapter
import com.ikhokha.viewmodels.CartViewModel
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : TopNavigationFragment(), CartItemsAdapter.ProductListener {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModel()
    private lateinit var cartItemsAdapter: CartItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel.products.observe(this) { onProductsSet(it) }
        cartViewModel.productsError.observe(this) { onNoProducts(it) }
        cartViewModel.deletedPosition.observe(this) { onProductDeleted(it) }
        cartViewModel.productDeleteError.observe(this) { onProductDeleteError(it) }
        cartViewModel.isCartCleared.observe(this) { onCartCleared() }
        cartViewModel.clearError.observe(this) { onCartClearError(it) }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel.products.value?.let {
            onProductsSet(it)
        }

        tbCart?.setNavigationOnClickListener {
            onBackPressed()
        }

        btnClear.setOnClickListener {
            showConfirmDialog(
                requireContext(),
                getString(com.ikhokha.common.R.string.confirm),
                getString(com.ikhokha.common.R.string.clear_cart_confirm),
                getString(com.ikhokha.common.R.string.clear),
                getString(com.ikhokha.common.R.string.cancel),
                {
                    cartViewModel.coroutineScope.launch(Dispatchers.IO) {
                        cartViewModel.clearItems()
                    }
                }
            )

        }

        btnCheckout.setOnClickListener {
            drawerController.navigateFromCartToSummary()
        }
    }

    override fun onTransitionAnimationComplete(oldFragment: BaseFragment?) {
        super.onTransitionAnimationComplete(oldFragment)

        cartViewModel.products.value?.let { /* No opp */ } ?: run {
            cartViewModel.coroutineScope.launch(Dispatchers.IO) {
                cartViewModel.getCartItems()
            }
        }
    }

    private fun onProductsSet(products: List<Product>) {
        val itemsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        itemsLayoutManager.initialPrefetchItemCount = products.size
        rvCartItems?.layoutManager = itemsLayoutManager
        cartItemsAdapter = CartItemsAdapter(
            requireContext(),
            products,
            com.ikhokha.features.common.R.layout.cart_item_layout
        )
        cartItemsAdapter.addProductListener(this)
        rvCartItems?.adapter = cartItemsAdapter

        rvCartItems.runWhenReady {
            cartViewModel.showLoading.value = false
        }
    }

    private fun onNoProducts(errorMessage: String) {
        cartViewModel.showLoading.value = false
    }

    private fun onProductDeleted(position: Int) {
        cartItemsAdapter.notifyItemRemoved(position)
        cartViewModel.coroutineScope.launch(Dispatchers.IO) {
            cartViewModel.checkCartItems()
        }
    }

    private fun onProductDeleteError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        )
    }

    override fun onProductClicked(product: Product, position: Int) {
        product.id?.let {
            drawerController.navigateFromCartToPreview(it)
        }
    }

    override fun onDeleteProductClicked(product: Product, position: Int) {
        cartViewModel.coroutineScope.launch(Dispatchers.IO) {
            cartViewModel.deleteItem(product, position)
        }
    }

    private fun onCartCleared() {
        cartItemsAdapter.notifyDataSetChanged()
        cartViewModel.coroutineScope.launch(Dispatchers.IO) {
            cartViewModel.checkCartItems()
        }
    }

    private fun onCartClearError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clParent.visibility = View.INVISIBLE
    }

    override fun onHardwareBackPressed() {
        super.onHardwareBackPressed()
        clParent.visibility = View.INVISIBLE
    }

    inline fun View.waitForLayout(crossinline yourAction: () -> Unit) {
        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                when {
                    vto.isAlive -> {
                        vto.removeOnGlobalLayoutListener(this)
                        yourAction()
                    }
                    else -> viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }
}