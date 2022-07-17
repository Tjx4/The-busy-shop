package com.ikhokha.repositories.products

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ikhokha.common.extensions.getProductFromDataSnapshot
import com.ikhokha.common.models.Product
import com.ikhokha.features.cart.core.room.CartDB
import com.ikhokha.core.persistance.room.tables.items.ItemsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductsRepositoryImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseStorage: FirebaseStorage,
    val cartDB: com.ikhokha.features.cart.core.room.CartDB
) :
    ProductsRepository {

    override val firebaseStorageRef: StorageReference
        get() = firebaseStorage.reference

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

    override suspend fun getCartItems(): List<Product>? {
        return try {
            val products = ArrayList<Product>()
            cartDB.itemsDAO.getAllItems()?.forEach {
                products.add(Product(it.id, it.description, it.image, it.price, it.quantity))
            }

            products

        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun getCartItemCount(): Int {
        return try {
            cartDB.itemsDAO.getAllItems()?.count() ?: 0
        } catch (exception: Exception) {
            0
        }
    }

    override suspend fun removeItemFromCart(product: Product): Boolean {
        return try {
            product.id?.let {
                val itemsTable = ItemsTable(it)
                cartDB.itemsDAO.delete(itemsTable)
            }
            true

        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun clearCart(): Boolean {
        return try {
            cartDB.itemsDAO.clear()
            true

        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun addProductToCart(product: Product): Boolean {
        return try {
            product.id?.let {
                val itemsTable = ItemsTable(it, product.description, product.image, product.price)
                cartDB.itemsDAO.insert(itemsTable)
            }
            true

        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun incrementCartProductQuantity(productId: String): Boolean {
        return try {
            cartDB.itemsDAO.get(productId)?.let { itemsTable ->
                itemsTable?.quantity = itemsTable.quantity + 1
                cartDB.itemsDAO.update(itemsTable)
            }
            true

        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun isProductExistsInCart(productId: String): Boolean {
        return try {
            val itemsTable = cartDB.itemsDAO.get(productId)
            itemsTable != null
        } catch (exception: Exception) {
            false
        }
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