package com.ikhokha.features.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.constants.PRODUCT_ID
import com.ikhokha.common.models.Product
import com.ikhokha.features.preview.databinding.FragmentPreviewBinding
import com.ikhokha.viewmodels.PreviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreviewFragment : SubNavigationFragment() {
    private lateinit var binding: FragmentPreviewBinding
    private val previewViewModel: PreviewViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewViewModel.product.observe(this) { onProductSet(it) }
        previewViewModel.productError.observe(this) { onProductError(it) }
        previewViewModel.addProduct.observe(this) { onNewProduct(it) }
        previewViewModel.incrementProduct.observe(this) { onProductExist(it) }
        previewViewModel.productAdded.observe(this) { onProductAdded(it) }
        previewViewModel.incrementedProduct.observe(this) { onProductIncremenrted() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview, container, false)
        binding.lifecycleOwner = this
        binding.previewViewModel = previewViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(PRODUCT_ID)?.let { productId ->
            //Todo: fix viewModelScope
            previewViewModel.getViewModelScope().launch(Dispatchers.IO) {
                previewViewModel.getProduct(productId)
            }
        }

    }

    fun onProductSet(product: Product) {
        //Todo: fix viewModelScope
        previewViewModel.getViewModelScope().launch(Dispatchers.IO) {
            previewViewModel.processProduct(product)
        }
    }

    fun onProductError(errorMessage: String) {
        //Show error alert
    }

    fun onNewProduct(product: Product) {
        //Todo: fix viewModelScope
        previewViewModel.getViewModelScope().launch(Dispatchers.IO) {
            previewViewModel.addProductToCart(product)
        }
    }

    fun onProductExist(product: Product) {
        //Todo: fix viewModelScope
        previewViewModel.getViewModelScope().launch(Dispatchers.IO) {
            previewViewModel.incrementProduct(product)
        }
    }

    fun onProductAdded(product: Product) {
        Toast.makeText(
            requireContext(),
            "${product.description} added to cart",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onProductIncremenrted() {
        Toast.makeText(
            requireContext(),
            "incremented",
            Toast.LENGTH_SHORT
        ).show()
    }
}