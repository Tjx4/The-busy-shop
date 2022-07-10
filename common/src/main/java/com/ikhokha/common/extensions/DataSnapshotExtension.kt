package com.ikhokha.common.extensions

import com.google.firebase.database.DataSnapshot
import com.ikhokha.common.models.Product

fun DataSnapshot.getProductFromDataSnapshot(): Product? {
    val product = this.getValue(Product::class.java)

    this.key?.let {
        product?.id = it
    }

    return product
}