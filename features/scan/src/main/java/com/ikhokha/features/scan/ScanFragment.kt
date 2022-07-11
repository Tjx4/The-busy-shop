package com.ikhokha.features.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.scan.databinding.FragmentScanBinding
import com.ikhokha.viewmodels.ScanViewModel
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment : TopNavigationFragment() {
    private lateinit var binding: FragmentScanBinding
    private val scanViewModel: ScanViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        drawerController.showBottomNav()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel.addProduct.observe(this) { onNewProduct(it) }
        scanViewModel.incrementProduct.observe(this) { onProductExist(it) }
        scanViewModel.cartItemCount.observe(this) { onCartItemsSet(it) }
        scanViewModel.noCartItems.observe(this) { onNoCartItems() }
        scanViewModel.incrementedProduct.observe(this) { onProductIncremented(it) }
        scanViewModel.incrementProductError.observe(this) { onProductIncrementError(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        binding.lifecycleOwner = this
        binding.scanViewModel = scanViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.checkCartItems()
        }

        btnTestProd.setOnClickListener {
            //Todo: fix viewModelScope
            scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
                val productId = btnTestProd.text.toString()
                scanViewModel.processProduct(productId)
            }
        }
    }

    fun onNewProduct(productId: String) {
        drawerController.navigateFromCartToPreview(productId)
    }

    fun onProductExist(productId: String) {
        scanViewModel.showLoading.value = true

        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.incrementProduct(productId)
        }
    }

    fun onProductIncremented(product: Product) {
        scanViewModel.showLoading.value = false

        Toast.makeText(
            requireContext(),
            getString(com.ikhokha.common.R.string.product_incremented, product.description),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onProductIncrementError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        )
    }

    fun onCartItemsSet(cartItemCount: Int) {
        drawerController.showBadge("$cartItemCount")
    }

    fun onNoCartItems() {
        drawerController.removeBadge()
    }

}