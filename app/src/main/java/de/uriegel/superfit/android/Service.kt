package de.uriegel.superfit.android

import android.app.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import de.uriegel.superfit.R
import de.uriegel.superfit.android.Application.Companion.CHANNEL_SERVICE_ID
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import de.uriegel.superfit.sensors.Searcher
import de.uriegel.superfit.ui.MainActivity

class Service : Service() {

    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        notification = NotificationCompat.Builder(this, CHANNEL_SERVICE_ID)
            .setContentTitle("Super Fit")
            .setContentText("Erfasst Fitness-Daten")
            .setSmallIcon(R.drawable.ic_bike)
            .setContentIntent(pendingIntent)
            .build()

        Searcher.start(this)
        LocationManager.start(this)

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager)
            .run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(20 * 3600_000)
                }
            }

        isRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()

        wakeLock.let {
            if (it.isHeld) {
                it.release()
            }
        }

        LocationManager.stop()
        HeartRate.stop()
        Bike.stop()
        Searcher.stop()

        isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    private lateinit var notification: Notification
    private lateinit var wakeLock: PowerManager.WakeLock

    companion object {
        var isRunning = false
            set(value){
                field = value
                listener?.invoke(isRunning)
            }

        fun setOnStateChangedListener(listener: ((isRunning: Boolean)->Unit)?) {
            this.listener = listener
            listener?.invoke(isRunning)
        }

        private var listener: ((isRunning: Boolean)->Unit)? = null
    }
}
