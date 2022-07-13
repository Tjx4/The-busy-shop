package com.ikhokha.features.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.Result
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.constants.SHORT_VIBRATION_DURATION
import com.ikhokha.common.helpers.*
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
    private lateinit var codeScanner: CodeScanner
    private val PERMISSION_REQUEST_CODE = 101
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

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
        initScanner()
        checkPermissions()
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.checkCartItems()
        }
    }

    override fun onResume() {
        super.onResume()
        if (areAllPermissionsGranted(requireContext(), PERMISSIONS)) {
            codeScanner.startPreview()
        }
    }

    fun checkPermissions() {
        when (areAllPermissionsGranted(requireContext(), PERMISSIONS)) {
            true -> {
                codeScanner.startPreview()
            }
            else -> requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode == PERMISSION_REQUEST_CODE && areAllPermissionsGranted(
            requireContext(),
            PERMISSIONS
        )) {
            true -> {
                codeScanner.startPreview()
            }
            else -> {
                showPermissionDialog()
            }
        }
    }

    private fun showPermissionDialog() {
        showConfirmDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.notice),
            getString(com.ikhokha.common.R.string.camera_permission),
            getString(com.ikhokha.common.R.string.request),
            getString(com.ikhokha.common.R.string.close),
            {
                requestPermissions()
            },
            {
                requireActivity().finish()
            }
        )
    }

    private fun requestPermissions() {
        requestNotGrantedPermissions(
            requireActivity() as AppCompatActivity,
            PERMISSIONS,
            PERMISSION_REQUEST_CODE
        )
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireActivity(), scanner_view).apply {
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::handleScannedBarcode)
            errorCallback = ErrorCallback(::scannerError)
        }
    }

    private fun handleScannedBarcode(result: Result) {
        vibratePhone(requireContext(), SHORT_VIBRATION_DURATION)
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            val productId = result.text
            scanViewModel.processProduct(productId)
        }
    }

    private fun scannerError(error: Throwable?) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            error?.message ?: getString(com.ikhokha.common.R.string.scanner_error),
            getString(com.ikhokha.common.R.string.close)
        )
    }

    private fun onNewProduct(productId: String) {
        drawerController.navigateFromScannerToPreview(productId)
    }

    private fun onProductExist(productId: String) {
        scanViewModel.showLoading.value = true
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.incrementProduct(productId)
        }
    }

    private fun onProductIncremented(product: Product) {
        scanViewModel.showLoading.value = false
        showSuccessDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.success),
            getString(com.ikhokha.common.R.string.product_incremented, product.description),
            getString(com.ikhokha.common.R.string.ok)
        ) {
            codeScanner.startPreview()
        }

    }

    private fun onProductIncrementError(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            errorMessage,
            getString(com.ikhokha.common.R.string.close)
        )
    }

    private fun onCartItemsSet(cartItemCount: Int) {
        drawerController.showBadge("$cartItemCount")
    }

    private fun onNoCartItems() {
        drawerController.removeBadge()
    }

}