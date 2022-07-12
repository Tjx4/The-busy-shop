package com.ikhokha.common.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun vibratePhone(context: Context, duration: Long, effect: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, effect))
    } else {
        vibrator.vibrate(duration)
    }
}