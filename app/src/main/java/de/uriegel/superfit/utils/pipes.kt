package de.uriegel.superfit.utils

import kotlin.math.floor

fun duration(value: Int): String {
    fun pad(num: Int, size: Int): String {
        var s = num.toString() + ""
        while (s.length < size)
            s = "0$s"
        return s
    }

    if (value > 0) {
        val hour = floor(value / 3600.0).toInt()
        val minRest = value % 3600
        val minute = floor(minRest / 60.0).toInt()
        val second = (minRest % 60)
        return if (hour > 0)
            "$hour:${pad(minute,2)}:${pad(second, 2)}"
        else
            "${pad(minute,2)}:${pad(second, 2)}"
    } else
        return "-"
}