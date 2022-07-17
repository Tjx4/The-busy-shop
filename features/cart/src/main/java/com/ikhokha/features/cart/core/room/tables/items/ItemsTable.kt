package com.ikhokha.features.cart.core.room.tables.items

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "items")
data class ItemsTable(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "description")
    var description: String? = null,
    @ColumnInfo(name = "image")
    var image: String? = null,
    @ColumnInfo(name = "price")
    var price: Double = 0.0,
    @ColumnInfo(name = "quantity")
    var quantity: Int = 1
) : Parcelable