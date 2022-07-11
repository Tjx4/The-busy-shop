package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showLoading: MutableLiveData<Boolean>
        get() = _showLoading

    private val _itemExist: MutableLiveData<Boolean> = MutableLiveData()
    val itemExist: MutableLiveData<Boolean>
        get() = _itemExist

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

    init {
        _showLoading.value = true
    }

    suspend fun isItemExist() {
        val productId = _product.value?.id ?: ""
        val itemExist = productsRepository.isProductAddedToCart(productId)

        withContext(Dispatchers.Main) {
            _itemExist.value = itemExist
        }
    }

    suspend fun getProduct(productId: String) {
        val product = productsRepository.getProduct(productId)

        withContext(Dispatchers.Main) {
            when (product) {
                null -> _productError.value = "Product not found"
                else -> _product.value = product
            }
        }
    }

     fun getFirebaseStorageRef(): StorageReference {
        return productsRepository.firebaseStorageRef
    }

    suspend fun addProductToCart() {
        val product = _product.value
        val response = product?.let { productsRepository.addProductToCart(it) }

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _productAddError.value = app.getString(com.ikhokha.common.R.string.product_add_error)
                else -> _productAdded.value = product
            }
        }
    }

}