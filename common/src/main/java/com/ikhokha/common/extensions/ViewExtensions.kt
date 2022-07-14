package com.ikhokha.common.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.LruCache
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("android:visibility")
fun View.setVisibility(visible: Boolean?) {
    visibility = if (visible != null && visible) View.VISIBLE else View.GONE
}

fun getScreenshotFromRecyclerView(view: RecyclerView): Bitmap? {
    val adapter = view.adapter
    var bigBitmap: Bitmap? = null
    if (adapter != null) {
        val size: Int = adapter.itemCount
        var height = 0
        val paint = Paint()
        var iHeight = 0f
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        val cacheSize = maxMemory / 8
        val bitmaCache: LruCache<String, Bitmap> = LruCache(cacheSize)
        for (i in 0 until size) {
            val holder: RecyclerView.ViewHolder =
                adapter.createViewHolder(view, adapter.getItemViewType(i))
            adapter.onBindViewHolder(holder, i)
            holder.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            holder.itemView.layout(
                0,
                0,
                holder.itemView.measuredWidth,
                holder.itemView.measuredHeight
            )
            holder.itemView.isDrawingCacheEnabled = true
            holder.itemView.buildDrawingCache()
            val drawingCache: Bitmap = holder.itemView.drawingCache
            if (drawingCache != null) {
                bitmaCache.put(i.toString(), drawingCache)
            }
            height += holder.itemView.measuredHeight
        }

        bigBitmap =
            Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)

        val bigCanvas = Canvas(bigBitmap)
        bigCanvas.drawColor(Color.WHITE)
        for (i in 0 until size) {
            val bitmap: Bitmap = bitmaCache.get(i.toString())
            bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint)
            iHeight += bitmap.height
            bitmap.recycle()
        }
    }
    return bigBitmap
}