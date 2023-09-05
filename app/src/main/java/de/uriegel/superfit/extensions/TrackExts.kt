package de.uriegel.superfit.extensions

import android.content.Context
import de.uriegel.superfit.room.Track
import java.util.Calendar
import java.util.Date

fun Track.getName(context: Context) =
    if (trackName?.isNotEmpty() == true)
        trackName!!
    else {
        val date = Date(time)
        val cal = Calendar.getInstance()
        cal.time = date
        val dateFormat = android.text.format.DateFormat.getDateFormat(context)
        "${dateFormat.format(date)} ${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${cal.get(
            Calendar.MINUTE).toString().padStart(2, '0')}"
    }

fun Track.getFileName() =
    Calendar.getInstance().let {
        it.time = Date(time)
        if (trackName?.isNotEmpty() == true)
            trackName!!
        else {
            "${it.get(Calendar.YEAR)}" +
                    "-${(it.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}" +
                    "-${it.get(Calendar.DATE).toString().padStart(2, '0')}" +
                    "-${it.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}" +
                    "-${it.get(Calendar.MINUTE).toString().padStart(2, '0')}"
        }
    }