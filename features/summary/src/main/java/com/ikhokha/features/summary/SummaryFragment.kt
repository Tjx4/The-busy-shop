package com.ikhokha.features.summary

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.base.fragment.SubNavigationFragment
import com.ikhokha.common.constants.*
import com.ikhokha.common.extensions.getScreenshotFromRecyclerView
import com.ikhokha.common.extensions.runWhenReady
import com.ikhokha.common.extensions.share
import com.ikhokha.common.helpers.getCurrentDateAndTime
import com.ikhokha.common.helpers.showConfirmDialog
import com.ikhokha.common.helpers.showErrorDialog
import com.ikhokha.common.models.Product
import com.ikhokha.features.cart.adapters.CartItemsAdapter
import com.ikhokha.features.summary.databinding.FragmentSummaryBinding
import com.ikhokha.viewmodels.SummaryViewModel
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class SummaryFragment : SubNavigationFragment(), com.ikhokha.features.cart.adapters.CartItemsAdapter.ProductListener {
    private lateinit var binding: FragmentSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModel()
    private lateinit var cartItemsAdapter: com.ikhokha.features.cart.adapters.CartItemsAdapter
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

        btnProcess.setOnClickListener {
            val heading = getString(com.ikhokha.common.R.string.summary)
            val description =
                getString(com.ikhokha.common.R.string.receipt_extra_text, getCurrentDateAndTime(DMYHM))

            shareSummaryPdf(heading, description)
        }
    }

    override fun onPermissionDeclined() {
        super.onPermissionDeclined()
        showPermissionDialog(
            getString(com.ikhokha.common.R.string.notice),
            getString(com.ikhokha.common.R.string.external_storage_permission),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        cartItemsAdapter = com.ikhokha.features.cart.adapters.CartItemsAdapter(
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

    private fun shareSummaryPdf(heading: String, description: String) {
        try {
            val pdfPath = Environment.getExternalStorageDirectory()
                .toString() + "/" + getCurrentDateAndTime(DMYHMSC) + "_" + SUMMARY_PDF

            val fileOutputStream = FileOutputStream(pdfPath)
            val document = Document()
            PdfWriter.getInstance(document, fileOutputStream)
            document.open()

            val drawable = requireActivity().getDrawable(com.ikhokha.common.R.drawable.ic_logo_dark)
            val logoBitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            logoBitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bitmapData = byteArrayOutputStream.toByteArray()
            val logo = Image.getInstance(bitmapData)
            logo.scaleAbsolute(
                requireActivity().resources.getDimension(com.ikhokha.common.R.dimen.pdf_logo_size),
                requireActivity().resources.getDimension(com.ikhokha.common.R.dimen.pdf_logo_size)
            )
            document.add(logo)

            val headingText = Font(
                Font.FontFamily.HELVETICA,
                requireActivity().resources.getDimension(com.ikhokha.common.R.dimen.pdf_sub_heading_text),
                Font.BOLD
            )
            val normalText = Font(
                Font.FontFamily.HELVETICA,
                requireActivity().resources.getDimension(com.ikhokha.common.R.dimen.pdf_medium_text),
                Font.NORMAL
            )
            val normalBoldText = Font(
                Font.FontFamily.HELVETICA,
                requireActivity().resources.getDimension(com.ikhokha.common.R.dimen.pdf_medium_text),
                Font.BOLD
            )

            val tittleParagraph = Paragraph(Chunk(getString(com.ikhokha.common.R.string.app_name), normalText))
            tittleParagraph.alignment = Element.ALIGN_LEFT
            document.add(tittleParagraph)

            document.add(Phrase("\n"))

            val headingParagraph = Paragraph(Chunk(getString(com.ikhokha.common.R.string.summary), headingText))
            headingParagraph.alignment = Element.ALIGN_CENTER
            document.add(headingParagraph)

            document.add(Phrase("\n"))

            val table = PdfPTable(3)

            val descriptionHeading = PdfPCell(
                Phrase(
                    Chunk(
                        getString(com.ikhokha.common.R.string.description),
                        normalBoldText
                    )
                )
            )
            table.addCell(descriptionHeading)

            val quantityHeading = PdfPCell(
                Phrase(
                    Chunk(
                        getString(com.ikhokha.common.R.string.quantity),
                        normalBoldText
                    )
                )
            )
            table.addCell(quantityHeading)

            val priceHeading = PdfPCell(
                Phrase(
                    Chunk(
                        getString(com.ikhokha.common.R.string.price),
                        normalBoldText
                    )
                )
            )
            table.addCell(priceHeading)

            summaryViewModel.products.value?.forEach {
                val description = PdfPCell(Phrase(Chunk(it.description, normalText)))
                description.border = Rectangle.NO_BORDER
                table.addCell(description)

                val quantity = PdfPCell(Phrase(Chunk("${it.quantity}", normalText)))
                quantity.border = Rectangle.NO_BORDER
                table.addCell(quantity)

                val price = PdfPCell(Phrase(Chunk("R${it.price}", normalText)))
                price.border = Rectangle.NO_BORDER
                table.addCell(price)
            }

            val emptyCell = PdfPCell(Phrase(""))
            emptyCell.border = Rectangle.NO_BORDER
            table.addCell(emptyCell)

            val grandTotalTextParagraph = Paragraph(
                Chunk(
                    "\n" + getString(com.ikhokha.common.R.string.total),
                    normalBoldText
                )
            )

            val grandTotalTextCell = PdfPCell(grandTotalTextParagraph)
            grandTotalTextCell.horizontalAlignment = Element.ALIGN_RIGHT
            grandTotalTextCell.border = Rectangle.NO_BORDER
            table.addCell(grandTotalTextCell)

            val grandTotalParagraph = Paragraph(
                Chunk("\nR${summaryViewModel.grandTotal.value}", normalBoldText)
            )

            val grandTotal = PdfPCell(grandTotalParagraph)
            grandTotal.border = Rectangle.NO_BORDER
            table.addCell(grandTotal)

            table.headerRows = 1
            document.add(table)

            document.add(Phrase("\n\n"))

            val orderDateParagraph = Paragraph(
                Chunk(getString(
                    com.ikhokha.common.R.string.order_date,
                    getCurrentDateAndTime(DMYHM)
                ), normalText)
            )
            orderDateParagraph.alignment = Element.ALIGN_CENTER
            document.add(orderDateParagraph)

            document.close()

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
                    shareSummaryPdf(heading, description)
                }
            )
        }
    }

    private fun shareCartImage(heading: String, description: String) {
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
}