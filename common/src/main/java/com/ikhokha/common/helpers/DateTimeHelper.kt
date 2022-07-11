package com.ikhokha.common.helpers

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDateAndTime(): String {
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
    return sdf.format(Date())

    //val current = LocalDateTime.now()
    //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    //return current.format(formatter)
}