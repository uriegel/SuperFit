package eu.selfhost.riegel.superfit.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun formatRfc3339(date: Date) = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date)
