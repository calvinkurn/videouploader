package com.videouploader

import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.floor

fun Long.toTimerFormat(): String {
    val seconds = (this % 60).toInt()
    val minutes = floor(this / 60.0).toInt()
    val hours = floor(this / 3600.0).toInt()

    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.nanoToSec(): Long {
    return floor(this / 1000000000.0).toLong()
}

fun Long.byteToMega(): Double {
    return this / 1000000.0
}

fun getUniqueStringTime(): String {
    return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(System.currentTimeMillis())
}