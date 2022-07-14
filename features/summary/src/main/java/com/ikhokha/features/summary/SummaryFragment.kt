package com.ikhokha.features.summary

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.extensions.getScreenshotFromRecyclerView
import com.ikhokha.common.extensions.runWhenReady
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.common.adapters.CartItemsAdapter
import com.ikhokha.features.summary.databinding.FragmentSummaryBinding
import com.ikhokha.viewmodels.SummaryViewModel
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class SummaryFragment : SubNavigationFragment(), CartItemsAdapter.ProductListener {
    private lateinit var binding: FragmentSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModel()
    private lateinit var cartItemsAdapter: CartItemsAdapter
    override var PERMISSION_REQUEST_CODE = 1001
    override var PERMISSIONS: Array<String>? = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summaryViewModel.products.observe(this) { onProductsSet(it) }
        summaryViewModel.productsError.observe(this) { onNoProducts(it) }
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

        summaryViewModel.products.value?.let {
            onProductsSet(it)
        }

        tbSummary?.setNavigationOnClickListener {
            onBackPressed()
        }

        btnProceed.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(com.ikhokha.common.R.string.proceed),
                Toast.LENGTH_SHORT
            ).show()

            shareReceipt()
        }
    }

    override fun onPermissionsGranted() {
        super.onPermissionsGranted()
        //Todo
        val p = 0
    }

    override fun onPermissionDeclined() {
        super.onPermissionDeclined()
        showPermissionDialog(
            getString(com.ikhokha.common.R.string.notice),
            getString(com.ikhokha.common.R.string.external_storage_permission),
            Manifest.permission.WRITE_EXTERNAL_STORAGE /*Todo: rethink show dialog due to one, handle many */
        )
    }

    private fun shareReceipt() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val img = getScreenshotFromRecyclerView(rvCartItems)
        imgtest.setImageBitmap(img)
        shareIntent.putExtra(Intent.EXTRA_STREAM, "img")
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        shareIntent.type = "application/pdf"
        startActivity(Intent.createChooser(shareIntent, "share"))
    }

    fun getPdf(){
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val file = File(pdfPath, "summary.pdf")
        val outputStream = FileOutputStream(file)


       // val pdfWriter = PdfQ(file)
        //val pdfDocument = PdfDocument(pdfWriter)
       // val document = Document(pdfDocument)
    }

    override fun onTransitionAnimationComplete(oldFragment: BaseFragment?) {
        super.onTransitionAnimationComplete(oldFragment)

        summaryViewModel.products.value?.let { /* No opp */ } ?: run {
            summaryViewModel.coroutineScope.launch(Dispatchers.IO) {
                summaryViewModel.getCartItems()
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
            com.ikhokha.features.common.R.layout.summary_item_layout
        )
        cartItemsAdapter?.addProductListener(this)
        rvCartItems?.adapter = cartItemsAdapter

        rvCartItems.runWhenReady {
            summaryViewModel.showLoading.value = false
            summaryViewModel.setGrandTotalPrice()
            summaryViewModel.setOrderDate()
        }
    }

    private fun onNoProducts(errorMessage: String) {
        showErrorDialog(
            requireContext(),
            getString(com.ikhokha.common.R.string.error),
            getString(com.ikhokha.common.R.string.no_summary_products),
            getString(com.ikhokha.common.R.string.close)
        )
    }

    override fun onProductClicked(product: Product, position: Int) {
        /* No opp */
    }

    override fun onDeleteProductClicked(product: Product, position: Int) {
        /* No opp */
    }
}