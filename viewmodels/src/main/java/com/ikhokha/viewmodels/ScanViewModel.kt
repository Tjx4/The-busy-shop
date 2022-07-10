package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScanViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

    private var _product: MutableLiveData<Product> = MutableLiveData()
    val product: MutableLiveData<Product>
        get() = _product

    private var _productError: MutableLiveData<String> = MutableLiveData()
    val productError: MutableLiveData<String>
        get() = _productError

    private var _productAdded: MutableLiveData<Product> = MutableLiveData()
    val productAdded: MutableLiveData<Product>
        get() = _productAdded

    private var _productAddError: MutableLiveData<String> = MutableLiveData()
    val addProductError: MutableLiveData<String>
        get() = _productAddError

    private var _incrementedProduct: MutableLiveData<Product> = MutableLiveData()
    val incrementedProduct: MutableLiveData<Product>
        get() = _incrementedProduct

    private var _incrementProductError: MutableLiveData<String> = MutableLiveData()
    val incrementProductError: MutableLiveData<String>
        get() = _incrementProductError

    private var _addProduct: MutableLiveData<Product> = MutableLiveData()
    val addProduct: MutableLiveData<Product>
        get() = _addProduct

    private var _incrementProduct: MutableLiveData<Product> = MutableLiveData()
    val incrementProduct: MutableLiveData<Product>
        get() = _incrementProduct

    suspend fun getProduct(productId: String) {
        val product = productsRepository.getProduct(productId)

        withContext(Dispatchers.Main) {
            when (product) {
                null -> _productError.value = "Product not found"
                else -> _product.value = product
            }
        }
    }

    //Todo: rename
    suspend fun processProduct(product: Product) {
        var cartProduct:Product? = null
        product?.id?.let {
            cartProduct  = productsRepository.getProductFromCart(it)
        }

        withContext(Dispatchers.Main) {
            when (cartProduct) {
                null -> _addProduct.value = product
                else -> _incrementProduct.value = product
            }
        }
    }

    suspend fun addProductToCart(product: Product) {
        val response = productsRepository.addProductToCart(product)

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _productAddError.value = "Error adding product"
                else -> _productAdded.value = product
            }
        }
    }

    suspend fun incrementProduct(product: Product) {
        val response = productsRepository.incrementCartProductQuantity(product.id ?: "")

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _incrementProductError.value = "Error incrementing product"
                else -> _incrementedProduct.value = product
            }
        }
    }
}