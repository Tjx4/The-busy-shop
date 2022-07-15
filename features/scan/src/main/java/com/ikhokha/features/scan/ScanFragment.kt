package com.ikhokha.features.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
    private var maxZoom: Int = 0
    private val zoomStep = 5
    override var PERMISSION_REQUEST_CODE = 101
    override var PERMISSIONS: Array<String>? = arrayOf(Manifest.permission.CAMERA)

    override fun onStart() {
        super.onStart()
        drawerController.showBottomNav()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel.addProduct.observe(this) { onNewProduct(it) }
        scanViewModel.incrementProduct.observe(this) { onExistingProduct(it) }
        scanViewModel.cartItemCount.observe(this) { onCartItemsSet(it) }
        scanViewModel.noCartItems.observe(this) { onNoCartItems() }
        scanViewModel.incrementedProduct.observe(this) { onProductIncremented(it) }
        scanViewModel.incrementProductError.observe(this) { onProductIncrementError(it) }
    }

    override fun onResume() {
        super.onResume()
        if(allPermissionsGranted){
            codeScanner.startPreview()
            scanViewModel.showLoading.value = false
        }
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
        initZoomSeekBar()
        onSeekBarChanged()
        onZoomDecreaseClicked()
        onZoomIncreaseClicked()
        codeScanner.startPreview()
        scanViewModel.showLoading.value = false

        scanViewModel.coroutineScope.launch(Dispatchers.IO) {
            scanViewModel.checkCartItems()
        }
    }

    override fun onPermissionDeclined() {
        super.onPermissionDeclined()
        showPermissionDialog(
            getString(com.ikhokha.common.R.string.notice),
            getString(com.ikhokha.common.R.string.camera_permission),
            Manifest.permission.CAMERA
       )
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireActivity(), scanner_view).apply {
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::onBarcodeScanned)
            errorCallback = ErrorCallback(::onScannerError)
        }
    }

    private fun initZoomSeekBar() {
        getCameraParameters(true)?.apply {
            this@ScanFragment.maxZoom = maxZoom
            seek_bar_zoom.max = maxZoom
            seek_bar_zoom.progress = zoom
        }
    }

    private fun onSeekBarChanged() {
        seek_bar_zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }
        })
    }

    private fun onZoomDecreaseClicked() {
        imgBtnDecreaseZoom.setOnClickListener {
            vibratePhone(requireContext(), SHORT_VIBRATION_DURATION)
            codeScanner.apply {
                if (zoom > zoomStep) {
                    zoom -= zoomStep
                } else {
                    zoom = 0
                }
                seek_bar_zoom.progress = zoom
            }
        }
    }

    private fun onZoomIncreaseClicked() {
        imgBtnIncreaseZoom.setOnClickListener {
            vibratePhone(requireContext(), SHORT_VIBRATION_DURATION)
            codeScanner.apply {
                if (zoom < maxZoom - zoomStep) {
                    zoom += zoomStep
                } else {
                    zoom = maxZoom
                }
                seek_bar_zoom.progress = zoom
            }
        }
    }

    private fun onBarcodeScanned(result: Result) {
        vibratePhone(requireContext(), SHORT_VIBRATION_DURATION)
        scanViewModel.coroutineScope.launch(Dispatchers.IO) {
            val productId = result.text
            scanViewModel.processProduct(productId)
        }
    }

    private fun onScannerError(error: Throwable?) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            error?.message ?: getString(com.ikhokha.common.R.string.scanner_error),
            getString(com.ikhokha.common.R.string.close)
        ) {
            codeScanner.startPreview()
        }
    }

    private fun onNewProduct(productId: String) {
        drawerController.navigateFromScannerToPreview(productId)
    }

    private fun onExistingProduct(productId: String) {
        scanViewModel.showLoading.value = true
        scanViewModel.coroutineScope.launch(Dispatchers.IO) {
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