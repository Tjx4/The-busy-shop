package com.ikhokha.repositories.products

import com.google.firebase.database.FirebaseDatabase
import com.ikhokha.common.models.Product
import com.ikhokha.core.persistance.room.CartDB
import com.ikhokha.repositories.products.ProductsRepository

class ProductsRepositoryImpl(cartDB: CartDB) : ProductsRepository {

    override suspend fun getProduct(productId: String): Product? {
        TODO("Not yet implemented")
        //firebaseDatabase
    }

    override suspend fun getCartProducts(): List<Product>? {
        TODO("Not yet implemented")
        //cartDB
    }

    override suspend fun removeProductFromCart(productId: String): Boolean {
        TODO("Not yet implemented")
        //cartDB
    }

    override suspend fun addProductToCart(product: Product): Boolean {
        TODO("Not yet implemented")
        //cartDB
    }

    override suspend fun incrementCartProductQuantity(product: Product): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getProductFromCart(productId: String): Product? {
        TODO("Not yet implemented")
    }
}