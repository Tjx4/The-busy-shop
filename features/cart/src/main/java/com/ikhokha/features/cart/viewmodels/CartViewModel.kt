package com.ikhokha.features.cart.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ikhokha.common.models.ItemsTable
import com.ikhokha.features.cart.core.room.tables.items.ItemsTable
import com.ikhokha.repositories.itemsTables.ItemsTablesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartViewModel(application: Application, val itemsTablesRepository: ItemsTablesRepository) :
    BaseViewModel(application) {

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showLoading: MutableLiveData<Boolean>
        get() = _showLoading

    private var _itemsTables: MutableLiveData<List<ItemsTable>> = MutableLiveData()
    val itemsTables: MutableLiveData<List<ItemsTable>>
        get() = _itemsTables

    private var _itemsTablesError: MutableLiveData<String> = MutableLiveData()
    val itemsTablesError: MutableLiveData<String>
        get() = _itemsTablesError

    private var _deletedPosition: MutableLiveData<Int> = MutableLiveData()
    val deletedPosition: MutableLiveData<Int>
        get() = _deletedPosition

    private var _itemsTableDeleteError: MutableLiveData<String> = MutableLiveData()
    val itemsTableDeleteError: MutableLiveData<String>
        get() = _itemsTableDeleteError

    private val _isCartCleared: MutableLiveData<Boolean> = MutableLiveData()
    val isCartCleared: MutableLiveData<Boolean>
        get() = _isCartCleared

    private var _clearError: MutableLiveData<String> = MutableLiveData()
    val clearError: MutableLiveData<String>
        get() = _clearError

    init {
        _showLoading.value = true
    }

    suspend fun getCartItems() {
        val itemsTables = itemsTablesRepository.getCartItems()

        withContext(Dispatchers.Main) {
            when (itemsTables.isNullOrEmpty()) {
                true -> _itemsTablesError.value = app.getString(com.ikhokha.features.cart.R.string.no_items)
                else -> _itemsTables.value = itemsTables
            }
        }
    }

    suspend fun deleteItem(itemsTable: ItemsTable, position: Int) {
        val response = itemsTablesRepository.removeItemFromCart(itemsTable)

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _itemsTableDeleteError.value = app.getString(com.ikhokha.features.cart.R.string.delete_error)
                else ->  {
                    (_itemsTables.value as ArrayList).remove(itemsTable)
                    _deletedPosition.value = position
                }
            }
        }
    }

    suspend fun clearItems() {
        val response = itemsTablesRepository.clearCart()

        withContext(Dispatchers.Main) {
            when (response) {
                false -> _clearError.value = app.getString(com.ikhokha.features.cart.R.string.clear_error)
                else ->  {
                    (_itemsTables.value as ArrayList).clear()
                    _isCartCleared.value = true
                }
            }
        }
    }

    suspend fun checkCartItems() {
        val itemCount = itemsTablesRepository.getCartItemCount()

        withContext(Dispatchers.Main) {
            if (itemCount < 1) {
                _itemsTablesError.value = app.getString(com.ikhokha.features.cart.R.string.no_items)
            }
        }
    }
}