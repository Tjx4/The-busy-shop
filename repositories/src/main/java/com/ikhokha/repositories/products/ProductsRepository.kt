package com.ikhokha.repositories.products

import com.google.firebase.storage.StorageReference
import com.ikhokha.common.models.Product

interface ProductsRepository {
    val firebaseStorageRef: StorageReference

    suspend fun getProduct(productId: String): Product?
    suspend fun getCartProducts(): List<Product>?
    suspend fun removeProductFromCart(productId: String): Boolean
    suspend fun addProductToCart(product: Product): Boolean
    suspend fun incrementCartProductQuantity(product: String): Boolean
    suspend fun getProductFromCart(productId: String): Product?
}