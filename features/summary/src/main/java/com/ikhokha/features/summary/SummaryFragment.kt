package com.ikhokha.features.summary

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.constants.ALL_IMAGE_TYPES
import com.ikhokha.common.extensions.getScreenshotFromRecyclerView
import com.ikhokha.common.extensions.runWhenReady
import com.ikhokha.common.extensions.share
import com.ikhokha.common.helpers.getCurrentDateAndTime
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.common.adapters.CartItemsAdapter
import com.ikhokha.features.summary.databinding.FragmentSummaryBinding
import com.ikhokha.viewmodels.SummaryViewModel
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    private fun shareReceipt() {
        val heading = getString(com.ikhokha.common.R.string.summary)
        val description =
            getString(com.ikhokha.common.R.string.receipt_extra_text, getCurrentDateAndTime())

        val cartItemsBitmap = getScreenshotFromRecyclerView(rvCartItems)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            cartItemsBitmap, heading, description
        )
        val uri = Uri.parse(path)

        requireActivity().share(
            heading,
            ALL_IMAGE_TYPES,
            description,
            uri
        )
    }


    fun getPdf() {
        /*
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pdfPath, "summary.pdf")
        val outputStream = FileOutputStream(file)


        // val pdfWriter = PdfQ(file)
        //val pdfDocument = PdfDocument(pdfWriter)
        // val document = Document(pdfDocument)


// create a new document
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pdfPath, "summary.pdf")
        val outputStream = FileOutputStream(file)

        val document = PdfDocument()
        // create a page description
        val pageInfo =  PdfDocument.PageInfo.Builder(100, 100, 1).create()

        // start a page
        val page = document.startPage(pageInfo)

        // draw something on the page
        val content = getContentView()
        content?.draw(page.canvas)

        // finish the page
        document.finishPage(page)

        // add more pages

        // write the document content
        document.writeTo(outputStream)

        // close the document
        document.close();
  */
    }

}