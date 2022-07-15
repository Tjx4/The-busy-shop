package com.ikhokha.common.helpers

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDateAndTime(format: String): String {
    val sdf = SimpleDateFormat(format)
    return sdf.format(Date())
}