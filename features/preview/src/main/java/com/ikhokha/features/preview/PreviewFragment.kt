package com.ikhokha.features.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.constants.PRODUCT_ID
import com.ikhokha.common.extensions.loadImageFromUrl
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.helpers.showSuccessDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.preview.databinding.FragmentPreviewBinding
import com.ikhokha.viewmodels.PreviewViewModel
import kotlinx.android.synthetic.main.fragment_preview.*
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
        previewViewModel.productAdded.observe(this) { onProductAdded(it) }
        previewViewModel.addProductError.observe(this) { onProductAddError(it) }
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
            previewViewModel.coroutineScope.launch(Dispatchers.IO) {
                previewViewModel.getProduct(productId)
            }
        }

        tbPreview?.setNavigationOnClickListener {
            onBackPressed()
        }

        btnAddToCart.setOnClickListener {
            previewViewModel.coroutineScope.launch(Dispatchers.IO) {
                previewViewModel.addProductToCart()
            }
        }

        btnScanAgain.setOnClickListener {
            drawerController.popBack()
        }
    }

    private fun onProductSet(product: Product) {
        previewViewModel.showLoading.value = false
        previewViewModel.coroutineScope.launch(Dispatchers.IO) {
            previewViewModel.isItemExist()
        }

        imgProduct.setImageResource(com.ikhokha.common.R.drawable.ic_placeholder)
        product.image?.let {
            val firebaseStorageRef = previewViewModel.getFirebaseStorageRef()
            val imageRef = firebaseStorageRef.child(it)
            imageRef.downloadUrl.addOnSuccessListener {
                val imageURL = it.toString()
                imgProduct?.loadImageFromUrl(
                    requireContext(),
                    imageURL,
                    com.ikhokha.common.R.drawable.ic_placeholder
                )
            }
        }
    }

    private fun onProductError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        ) {
            onBackPressed()
        }
    }

    private fun onProductAdded(product: Product) {
        showSuccessDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.success),
            getString(com.ikhokha.common.R.string.product_added, product.description),
            getString(com.ikhokha.common.R.string.ok)
        ) {
            onBackPressed()
        }
    }

    private fun onProductAddError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        ) {
            onBackPressed()
        }
    }

}