package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SummaryViewModel(application: Application, val productsRepository: ProductsRepository) : BaseViewModel(application)  {

    private var _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: MutableLiveData<List<Product>>
        get() = _products

    private var _productsError: MutableLiveData<String> = MutableLiveData()
    val productsError: MutableLiveData<String>
        get() = _productsError

    suspend fun getCartProducts() {
        val products = productsRepository.getCartProducts()

        withContext(Dispatchers.Main) {
            when (products.isNullOrEmpty()) {
                true -> _productsError.value = "No items found"
                else -> _products.value = products
            }
        }
    }
}