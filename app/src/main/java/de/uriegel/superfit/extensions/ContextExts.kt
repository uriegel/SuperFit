package de.uriegel.superfit.extensions

import android.content.Context
import android.content.Intent
import android.os.Build
import de.uriegel.superfit.android.Service

fun Context.startService() {
    val startIntent = Intent(this, Service::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        startForegroundService(startIntent)
    else
        startService(startIntent)
}

fun Context.stopService() {
    val startIntent = Intent(this, Service::class.java)
    stopService(startIntent)
}
