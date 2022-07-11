package com.ikhokha.common.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun getCurrentDateAndTime(): String {
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    return sdf.format(Date())

    //val current = LocalDateTime.now()
    //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    //return current.format(formatter)
}