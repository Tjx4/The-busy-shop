package com.ikhokha.repositories.products

import com.google.firebase.storage.StorageReference
import com.ikhokha.common.models.Product

interface ProductsRepository {
    val firebaseStorageRef: StorageReference

    suspend fun getProduct(productId: String): Product?
    suspend fun getCartItems(): List<Product>?
    suspend fun getCartItemCount(): Int
    suspend fun removeProductFromCart(product: Product): Boolean
    suspend fun addProductToCart(product: Product): Boolean
    suspend fun incrementCartProductQuantity(product: String): Boolean
    suspend fun isProductAddedToCart(productId: String): Boolean //Todo: rename
}