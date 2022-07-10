package com.ikhokha.features.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.models.Product
import com.ikhokha.features.scan.databinding.FragmentScanBinding
import com.ikhokha.viewmodels.ScanViewModel
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private val scanViewModel: ScanViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel.product.observe(this) { onProductSet(it) }
        scanViewModel.addProduct.observe(this) { onNewProduct(it) }
        scanViewModel.incrementProduct.observe(this) { onProductExist(it) }
        scanViewModel.productAdded.observe(this) { onProductAdded(it) }
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
        btnTestProd.setOnClickListener {

            //Todo: fix viewModelScope
            scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
                val productId = btnTestProd.text.toString()
                scanViewModel.getProduct(productId)
            }
        }
    }

    fun onProductSet(product: Product) {
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.processProduct(product)
        }
    }

    fun onNewProduct(product: Product) {
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.addProductToCart(product)
        }
    }

    fun onProductExist(product: Product) {
        //Todo: fix viewModelScope
        scanViewModel.getViewModelScope().launch(Dispatchers.IO) {
            scanViewModel.incrementProduct(product)
        }
    }

    fun onProductAdded(product: Product) {
        Toast.makeText(
            requireContext(),
            "${product.description} added to cart",
            Toast.LENGTH_SHORT
        ).show()
    }

}