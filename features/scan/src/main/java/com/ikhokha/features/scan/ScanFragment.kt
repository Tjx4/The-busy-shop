package com.ikhokha.features.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.TopNavigationFragment
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
        scanViewModel.cartItemCount.observe(this) { onCartItemsSet(it) }
        scanViewModel.noCartItems.observe(this) { onNoCartItems() }
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
            val productId = btnTestProd.text.toString()
            onProductSet(productId)
        }

    }

    fun onProductSet(productId: String) {
        drawerController.navigateFromPreviewnerToPreview(productId)
    }

    fun onCartItemsSet(cartItemCount: Int) {
        drawerController.showBadge("$cartItemCount")
    }

    fun onNoCartItems() {
        drawerController.removeBadge()
    }

}