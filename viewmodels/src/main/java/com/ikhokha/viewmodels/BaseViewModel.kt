package com.ikhokha.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel(val app: Application) : AndroidViewModel(app) {
    //Todo: remove
    fun getViewModelScope(): CoroutineScope {
        return viewModelScope
    }
}