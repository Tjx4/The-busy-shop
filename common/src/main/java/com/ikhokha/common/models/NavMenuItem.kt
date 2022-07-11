package com.ikhokha.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NavMenuItem(
    var index: Int,
    var fragment: Int
) : Parcelable