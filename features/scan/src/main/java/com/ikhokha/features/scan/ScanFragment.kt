package com.ikhokha.features.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ikhokha.features.scan.databinding.FragmentScanBinding
import com.ikhokha.viewmodels.ScanViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private val scanViewModel: ScanViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //scanViewModel.products.observe(this) { onProductsSet(it) }
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
}