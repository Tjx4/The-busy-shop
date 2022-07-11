package com.ikhokha.common.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @Exclude @SerializedName("id")
    var id: String? = null,
    @PropertyName("description") @SerializedName("description")
    var description: String? = null,
    @PropertyName("image") @SerializedName("image")
    var image: String? = null,
    @PropertyName("price") @SerializedName("price")
    var price: Double = 0.0,
    @Exclude
    var quantity: Int = 1
) : Parcelable