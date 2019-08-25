@file:Suppress("DEPRECATION")

package eu.selfhost.riegel.superfit.utils

import java.util.*
import kotlin.math.abs

fun formatRfc3339(time: Long, timeOffset: Long): String {
    val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
    val date = (Date(time + timeOffset - timeZone))
    return "${date.year + 1900}" +
            "-${(date.month + 1).toString().padStart(2, '0')}" +
            "-${date.date.toString().padStart(2, '0')}" +
            "T${date.hours.toString().padStart(2, '0')}" +
            ":${date.minutes.toString().padStart(2, '0')}" +
            ":${date.seconds.toString().padStart(2, '0')}" +
            "${if (timeOffset > 0) '+' else '-'}" +
            (abs(timeOffset / 3600000)).toString().padStart(2, '0') +
            "00"
}




