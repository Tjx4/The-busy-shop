package com.ikhokha.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NavMenuItem(
    var fragment: Int,
    var index: Int
) : Parcelable