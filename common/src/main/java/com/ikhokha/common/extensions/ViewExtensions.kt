package com.ikhokha.common.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
fun View.setVisibility(visible: Boolean?) {
    visibility = if (visible != null && visible) View.VISIBLE else View.GONE
}