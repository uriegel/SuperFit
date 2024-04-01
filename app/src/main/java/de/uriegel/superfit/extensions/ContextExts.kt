package de.uriegel.superfit.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import de.uriegel.superfit.android.Service
import java.io.FileInputStream

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

fun Context.saveOpen(uri: Uri): FileInputStream? =
    try {
        contentResolver.openInputStream(uri) as FileInputStream
    } catch (e: Exception) {
        null
    }
