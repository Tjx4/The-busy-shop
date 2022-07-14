package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ikhokha.common.helpers.getCurrentDateAndTime
import com.ikhokha.common.helpers.getTotalPrice
import com.ikhokha.common.models.Product
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SummaryViewModel(application: Application, val productsRepository: ProductsRepository) : BaseViewModel(application)  {

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showLoading: MutableLiveData<Boolean>
        get() = _showLoading

    private var _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: MutableLiveData<List<Product>>
        get() = _products

    private var _productsError: MutableLiveData<String> = MutableLiveData()
    val productsError: MutableLiveData<String>
        get() = _productsError

    private var _grandTotal: MutableLiveData<Double> = MutableLiveData(0.0)
    val grandTotal: MutableLiveData<Double>
        get() = _grandTotal

    private var _orderDateAndTime: MutableLiveData<String> = MutableLiveData()
    val orderDateAndTime: MutableLiveData<String>
        get() = _orderDateAndTime

    init {
        _showLoading.value = true
    }

    suspend fun getCartItems() {
        val products = productsRepository.getCartItems()

        withContext(Dispatchers.Main) {
            when (products.isNullOrEmpty()) {
                true -> _productsError.value = app.getString(com.ikhokha.common.R.string.no_summary_items)
                else -> _products.value = products
            }
        }
    }

    fun setGrandTotalPrice()  {
        var grandTotal = 0.0
        _products.value?.forEach {
            grandTotal += getTotalPrice(it.price, it.quantity)
        }
        _grandTotal.value = grandTotal
    }

    fun setOrderDate()  {
        _orderDateAndTime.value = app.getString(com.ikhokha.common.R.string.order_date, getCurrentDateAndTime())
    }

}