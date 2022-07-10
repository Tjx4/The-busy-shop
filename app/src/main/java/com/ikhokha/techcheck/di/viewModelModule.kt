package com.ikhokha.techcheck.di

import com.ikhokha.viewmodels.CartViewModel
import com.ikhokha.viewmodels.SummaryViewModel
import com.ikhokha.viewmodels.PreviewViewModel
import com.ikhokha.viewmodels.ScanViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ScanViewModel(androidApplication(), get()) }
    viewModel { PreviewViewModel(androidApplication(), get()) }
    viewModel { SummaryViewModel(androidApplication(), get()) }
    viewModel { CartViewModel(androidApplication(), get()) }
}