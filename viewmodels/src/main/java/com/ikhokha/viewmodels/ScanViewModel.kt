package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScanViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showLoading: MutableLiveData<Boolean>
        get() = _showLoading

    private var _addProduct: MutableLiveData<String> = MutableLiveData()
    val addProduct: MutableLiveData<String>
        get() = _addProduct

    private var _incrementProduct: MutableLiveData<String> = MutableLiveData()
    val incrementProduct: MutableLiveData<String>
        get() = _incrementProduct

    private val _cartItemCount: MutableLiveData<Int> = MutableLiveData()
    val cartItemCount: MutableLiveData<Int>
        get() = _cartItemCount

    private val _noCartItems: MutableLiveData<Boolean> = MutableLiveData()
    val noCartItems: MutableLiveData<Boolean>
        get() = _noCartItems

    private var _incrementedProduct: MutableLiveData<Product> = MutableLiveData()
    val incrementedProduct: MutableLiveData<Product>
        get() = _incrementedProduct

    private var _incrementProductError: MutableLiveData<String> = MutableLiveData()
    val incrementProductError: MutableLiveData<String>
        get() = _incrementProductError

    suspend fun checkCartItems() {
        val itemCount = productsRepository.getCartItemCount()

        withContext(Dispatchers.Main) {
            when (itemCount > 0) {
                true -> _cartItemCount.value = itemCount
                else -> _noCartItems.value = true
            }
        }
    }

    suspend fun processProduct(productId: String) {
        var productExist = productsRepository.isProductExistsInCart(productId)

        withContext(Dispatchers.Main) {
            when (productExist) {
                false -> _addProduct.value = productId
                else -> _incrementProduct.value = productId
            }
        }
    }

    suspend fun incrementProduct(productId: String) {
        val response = productsRepository.incrementCartProductQuantity(productId) //Todo: maybe return product description

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _incrementProductError.value =
                    app.getString(com.ikhokha.common.R.string.increment_error)
                else -> _incrementedProduct.value = productsRepository.getProduct(productId)
            }
        }
    }
}