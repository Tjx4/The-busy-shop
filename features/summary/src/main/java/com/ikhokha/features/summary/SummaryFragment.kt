package com.ikhokha.features.summary

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.internal.ViewUtils.getContentView
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.constants.ALL_IMAGE_TYPES
import com.ikhokha.common.constants.PDF_TYPE
import com.ikhokha.common.extensions.getScreenshotFromRecyclerView
import com.ikhokha.common.extensions.runWhenReady
import com.ikhokha.common.extensions.share
import com.ikhokha.common.helpers.getCurrentDateAndTime
import com.ikhokha.common.helpers.showConfirmDialog
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.common.adapters.CartItemsAdapter
import com.ikhokha.features.summary.databinding.FragmentSummaryBinding
import com.ikhokha.viewmodels.SummaryViewModel
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfDocument
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.TextField
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SummaryFragment : SubNavigationFragment(), CartItemsAdapter.ProductListener {
    private lateinit var binding: FragmentSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModel()
    private lateinit var cartItemsAdapter: CartItemsAdapter
    override var PERMISSION_REQUEST_CODE = 1001
    override var PERMISSIONS: Array<String>? = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

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

        /*
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
        */

        createPdf(heading, description)
    }


    fun createPdf(heading: String, description: String) {
        try {
            val summaryDocument = "summary.pdf"
            val pdfPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()

            val file = File(pdfPath, summaryDocument)
            val outputStream = FileOutputStream(file)

            /*
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)

            val paragraph = Paragraph("Hello world")
            */

            val pdfDocument = PdfDocument()
            PdfWriter.getInstance(pdfDocument, outputStream)

            val paragraph = Paragraph("Hello world")


          //  val boldText = TextField("Bold").setBold()
         //   val paragraph2 = Paragraph("Hello world")
          //  paragraph2.add(boldText)

            val byteArrayOutputStream = ByteArrayOutputStream()
            val cartItemsBitmap = getScreenshotFromRecyclerView(rvCartItems)
            cartItemsBitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bitmapData = byteArrayOutputStream.toByteArray()
            //
            //val image = Image(cartItemsBitmap)

            pdfDocument.add(paragraph)
            // pdfDocument.add(paragraph2)
            // pdfDocument.add(image)
            pdfDocument.close()


            val uri = Uri.parse(pdfPath)
            requireActivity().share(
                heading,
                PDF_TYPE,
                description,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

        } catch (exception: Exception) {
            showConfirmDialog(
                requireContext(),
                getString(com.ikhokha.common.R.string.error),
                exception.message ?: getString(com.ikhokha.common.R.string.pdf_error),
                getString(com.ikhokha.common.R.string.try_again),
                getString(com.ikhokha.common.R.string.close),
                {
                    createPdf(heading, description)
                }
            )
        }
    }
}