package com.ikhokha.features.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.ikhokha.common.models.Product
import com.ikhokha.features.summary.databinding.FragmentSummaryBinding
import com.ikhokha.viewmodels.SummaryViewModel
import kotlinx.android.synthetic.main.fragment_summary.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SummaryFragment : SubNavigationFragment() {
    private lateinit var binding: FragmentSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModel()
    //private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summaryViewModel.products.observe(this) { onProductsSet(it) }
        summaryViewModel.productsError.observe(this) { onProductsError(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary, container, false)
        binding.lifecycleOwner = this
        binding.summaryViewModel = summaryViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onProductsSet(product: List<Product>) {

    }

    private fun onProductsError(product: String) {

    }

}