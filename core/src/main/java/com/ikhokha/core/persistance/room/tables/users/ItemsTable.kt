package com.ikhokha.core.persistance.room.tables.users

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
    var id: String?,
    @ColumnInfo(name = "description")
    var description: String?,
    @ColumnInfo(name = "image")
    var image: String?,
    @ColumnInfo(name = "price")
    var price: Double = 0.0,
    @ColumnInfo(name = "price")
    var quantity: Int = 1
) : Parcelable