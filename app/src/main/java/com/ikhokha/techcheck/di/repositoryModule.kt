package com.ikhokha.techcheck.di

import org.koin.dsl.module

val repositoryModule = module {
    single<ProductsRepository> { ProductsRepositoryImpl(get(), get()) }
}