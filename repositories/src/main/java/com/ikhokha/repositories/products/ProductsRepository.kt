package com.ikhokha.repositories.products

import com.google.firebase.storage.StorageReference
import com.ikhokha.common.models.Product

interface ProductsRepository {
    val firebaseStorageRef: StorageReference

    suspend fun getProduct(productId: String): Product?
    suspend fun getCartItems(): List<Product>?
    suspend fun getCartItemCount(): Int
    suspend fun removeItemFromCart(product: Product): Boolean
    suspend fun clearCart(): Boolean
    suspend fun addProductToCart(product: Product): Boolean
    suspend fun incrementCartProductQuantity(product: String): Boolean
    suspend fun isProductExistsInCart(productId: String): Boolean
}