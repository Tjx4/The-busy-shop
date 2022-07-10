package com.ikhokha.techcheck.di

import com.ikhokha.repositories.products.ProductsRepository
import com.ikhokha.repositories.products.ProductsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ProductsRepository> { ProductsRepositoryImpl(get()) }
}