package com.ikhokha.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun ImageView.loadImageFromUrl(context: Context, url: String, placeHolderPic: Int?, successCallback: () -> Unit = {}, errorCallback: () -> Unit = {}) {
    val imageView = this
    val requestBuilder = Glide.with(context).load(url)
    placeHolderPic?.let {
        requestBuilder.placeholder(it)
    }
    requestBuilder.fitCenter()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                errorCallback.invoke()
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                imageView.setImageDrawable(resource)
                successCallback.invoke()
                return true
            }
        })
        .into(this)
}

fun ImageView.loadImageFromBitmap(context: Context, bitmap: Bitmap, placeHolderPic: Int) {
    Glide.with(context).load(bitmap).placeholder(placeHolderPic).fitCenter().into(this)
}

fun ImageView.loadImageFromResources(context: Context, res: Int, placeHolderPic: Int) {
    Glide.with(context).load(res).placeholder(placeHolderPic).fitCenter().into(this)
}
