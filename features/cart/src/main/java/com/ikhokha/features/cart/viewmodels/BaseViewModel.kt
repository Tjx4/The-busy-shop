package com.ikhokha.features.cart.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel(val app: Application) : AndroidViewModel(app) {
    val coroutineScope: CoroutineScope
        get() = viewModelScope
}