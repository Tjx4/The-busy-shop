package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScanViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

    private val _cartItemCount: MutableLiveData<Int> = MutableLiveData()
    val cartItemCount: MutableLiveData<Int>
        get() = _cartItemCount

    private val _noCartItems: MutableLiveData<Boolean> = MutableLiveData()
    val noCartItems: MutableLiveData<Boolean>
        get() = _noCartItems

    suspend fun checkCartItems() {
        val itemCount = productsRepository.getCartItemCount()

        withContext(Dispatchers.Main) {
            when (itemCount > 0) {
                 true -> _cartItemCount.value = itemCount
                 else -> _noCartItems.value = true
            }
        }
    }

}