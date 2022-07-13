package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    //Todo: rename
    private val _cartClear: MutableLiveData<Boolean> = MutableLiveData()
    val cartClear: MutableLiveData<Boolean>
        get() = _cartClear

    private var _clearError: MutableLiveData<String> = MutableLiveData()
    val clearError: MutableLiveData<String>
        get() = _clearError

    init {
        _showLoading.value = true
    }

    suspend fun getCartItems() {
        val products = productsRepository.getCartItems()

        withContext(Dispatchers.Main) {
            when (products.isNullOrEmpty()) {
                true -> _productsError.value = app.getString(com.ikhokha.common.R.string.no_items)
                else -> _products.value = products
            }
        }
    }

    suspend fun deleteItem(product: Product, position: Int) {
        val response = productsRepository.removeItemFromCart(product)

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _productDeleteError.value = app.getString(com.ikhokha.common.R.string.delete_error)
                else ->  {
                    (_products.value as ArrayList).remove(product)
                    _deletedPosition.value = position
                }
            }
        }
    }

    suspend fun clearItems() {
        val response = productsRepository.clearCart()

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _clearError.value = app.getString(com.ikhokha.common.R.string.clear_error)
                else ->  {
                    (_products.value as ArrayList).clear()
                    _cartClear.value = true
                }
            }
        }
    }

    suspend fun checkCartItems() {
        val itemCount = productsRepository.getCartItemCount()

        withContext(Dispatchers.Main) {
            if (itemCount < 1) {
                _productsError.value = app.getString(com.ikhokha.common.R.string.no_items)
            }
        }
    }
}