package de.uriegel.superfit.extensions

import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import kotlin.math.floor

fun Long.formatRfc3339(): String {
    val date = Date(this)
    val cal = Calendar.getInstance()
    cal.time = date
    val timeOffset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
    return "${cal.get(Calendar.YEAR)}" +
            "-${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}" +
            "-${cal.get(Calendar.DATE).toString().padStart(2, '0')}" +
            "T${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}" +
            ":${cal.get(Calendar.MINUTE).toString().padStart(2, '0')}" +
            ":${cal.get(Calendar.SECOND).toString().padStart(2, '0')}" +
            "${if (timeOffset > 0) '+' else '-'}" +
            (abs(timeOffset / 3600000)).toString().padStart(2, '0') +
            "00"
}

fun Int?.displayFormat(): String =
    this?.let {
        if (it != -1) it.toString() else "-"
    } ?: ""

fun Int?.displayDuration(): String {
    fun pad(num: Int, size: Int): String {
        var s = num.toString() + ""
        while (s.length < size)
            s = "0$s"
        return s
    }

    return this?.let {
        if (it > 0) {
            val hour = floor(it / 3600.0).toInt()
            val minRest = it % 3600
            val minute = floor(minRest / 60.0).toInt()
            val second = (minRest % 60)
            if (hour > 0)
                "$hour:${pad(minute,2)}:${pad(second, 2)}"
            else
                "${pad(minute,2)}:${pad(second, 2)}"
        } else
            "-"
    } ?: ""
}
