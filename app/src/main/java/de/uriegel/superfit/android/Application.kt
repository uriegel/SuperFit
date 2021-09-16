@file:Suppress("unused")

package de.uriegel.superfit.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

//import org.mapsforge.map.android.graphics.AndroidGraphicFactory

class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
  //      AndroidGraphicFactory.createInstance(this)
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_SERVICE_ID,
                "CHANNEL_SERVICE", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for foreground service"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: Application
            private set
        const val CHANNEL_SERVICE_ID = "CHANNEL_SERVICE"
    }
}


