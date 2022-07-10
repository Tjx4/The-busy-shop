package com.ikhokha.viewmodels

import android.app.Application
import com.ikhokha.repositories.products.ProductsRepository

class ScanViewModel(application: Application, val productsRepository: ProductsRepository) :
    BaseViewModel(application) {

}