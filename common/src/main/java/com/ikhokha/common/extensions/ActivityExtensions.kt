package com.ikhokha.common.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Display
import com.ikhokha.common.R
import com.ikhokha.common.constants.ACTIVITY_TRANSITION
import com.ikhokha.common.constants.PAYLOAD_KEY
import com.ikhokha.common.models.Transition

val FADE_IN_ACTIVITY = getTransitionAnimation(R.anim.fade_in, R.anim.no_transition)
val FADE_OUT_ACTIVITY = getTransitionAnimation(R.anim.no_transition, R.anim.fade_out)

fun Activity.navigateToActivity(
    className: String,
    payload: Bundle?,
    transitionAnimation: Transition
) {
    goToActivity(className, transitionAnimation, payload)
}

private fun Activity.goToActivity(
    className: String,
    transitionAnimation: Transition,
    payload: Bundle?
) {
    val intent = Intent(this, Class.forName(className))

    val fullPayload = payload ?: Bundle()
    fullPayload.putIntArray(
        ACTIVITY_TRANSITION, intArrayOf(
            transitionAnimation.inAnimation,
            transitionAnimation.outAnimation
        )
    )

    intent.putExtra(PAYLOAD_KEY, fullPayload)
    startActivity(intent)
}

private fun getTransitionAnimation(inAnimation: Int, outAnimation: Int): Transition {
    val transitionProvider = Transition()
    transitionProvider.inAnimation = inAnimation
    transitionProvider.outAnimation = outAnimation
    return transitionProvider
}

fun Activity.getScreenCols(columnWidthDp: Float): Int {
    val displayMetrics = this.resources.displayMetrics
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / columnWidthDp + 0.5).toInt()
}

fun Activity.getScreenWidth(): Int {
    var Measuredwidth = 0
    //var Measuredheight = 0
    val size = Point()
    val w = windowManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        windowManager.defaultDisplay.getSize(size)
        Measuredwidth = size.x
        // Measuredheight = size.y
    } else {
        val display: Display = windowManager.defaultDisplay
        Measuredwidth = display.getWidth()
        //Measuredheight = display.getHeight()
    }

    return Measuredwidth
}