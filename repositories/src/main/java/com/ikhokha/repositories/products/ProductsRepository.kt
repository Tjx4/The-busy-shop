package com.ikhokha.repositories.products

import com.ikhokha.common.models.Product

interface ProductsRepository {
    suspend fun getProduct(productId: String): Product?
    suspend fun getCartProducts(): List<Product>?
    suspend fun removeProductFromCart(productId: String): Boolean
    suspend fun addProductToCart(product: Product): Boolean
    suspend fun incrementCartProductQuantity(product: Product): Boolean
    suspend fun getProductFromCart(productId: String): Product?
}