package de.uriegel.superfit.maps

import java.util.*
import kotlin.math.abs

fun formatRfc3339(time: Long, timeOffset: Long): String {
    val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
    val date = (Date(time + timeOffset - timeZone))
    val cal = Calendar.getInstance()
    cal.time = date
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




