package com.ikhokha.repositories.products

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ikhokha.common.extensions.getProductFromDataSnapshot
import com.ikhokha.common.models.Product
import com.ikhokha.core.persistance.room.CartDB
import com.ikhokha.core.persistance.room.tables.items.ItemsTable
import com.ikhokha.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductsRepositoryImpl(val firebaseDatabase: FirebaseDatabase, val cartDB: CartDB) :
    ProductsRepository {

    override suspend fun getProduct(productId: String): Product? {
        var product: Product? = null
        val query = firebaseDatabase.reference.child(productId)
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                query.addListenerForSingleValueEvent(
                    FValueEventListener(
                        onDataChange = {
                            product = it.getProductFromDataSnapshot()
                            continuation.resume(it)
                        },
                        onError = {
                            continuation.resumeWithException(it.toException())
                        })
                )
            }
        }

        return product
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
        return try {
            product.id?.let {
                val itemsTable = ItemsTable(it, product.description, product.image, product.price)
                cartDB.itemsDAO.insert(itemsTable)
                true
            }

            false
        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun incrementCartProductQuantity(productId: String): Boolean {
        return try {
            cartDB.itemsDAO.get(productId)?.let { itemsTable ->
                itemsTable?.quantity = itemsTable.quantity + 1
                cartDB.itemsDAO.update(itemsTable)
                true
            }

            false
        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun getProductFromCart(productId: String): Product? {
        TODO("Not yet implemented")
    }

    class FValueEventListener(
        val onDataChange: (DataSnapshot) -> Unit,
        val onError: (DatabaseError) -> Unit
    ) :
        ValueEventListener {
        override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
        override fun onCancelled(error: DatabaseError) = onError.invoke(error)
    }
}