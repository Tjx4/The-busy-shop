package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showLoading: MutableLiveData<Boolean>
        get() = _showLoading

    private var _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: MutableLiveData<List<Product>>
        get() = _products

    private var _productsError: MutableLiveData<String> = MutableLiveData()
    val productsError: MutableLiveData<String>
        get() = _productsError

    private var _deletedPosition: MutableLiveData<Int> = MutableLiveData()
    val deletedPosition: MutableLiveData<Int>
        get() = _deletedPosition

    private var _productDeleteError: MutableLiveData<String> = MutableLiveData()
    val productDeleteError: MutableLiveData<String>
        get() = _productDeleteError

    init {
        _showLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getProducts()
        }
    }

    suspend fun getProducts() {
        val products = productsRepository.getCartProducts()

        withContext(Dispatchers.Main) {
            when (products.isNullOrEmpty()) {
                true -> _productsError.value = app.getString(com.ikhokha.common.R.string.no_items)
                else -> _products.value = products
            }
        }
    }

    suspend fun deleteProduct(productId: String?, position: Int) {
        val response = productId?.let { productsRepository.removeProductFromCart(it) }

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _productDeleteError.value = app.getString(com.ikhokha.common.R.string.delete_error)
                else -> _deletedPosition.value = position
            }
        }
    }

}