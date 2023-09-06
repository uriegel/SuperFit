package de.uriegel.superfit.extensions

import java.util.Calendar
import java.util.Date
import kotlin.math.abs

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