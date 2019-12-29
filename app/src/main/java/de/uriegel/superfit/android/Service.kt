package de.uriegel.superfit.android

import android.app.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import de.uriegel.superfit.R
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import de.uriegel.superfit.sensors.Searcher
import de.uriegel.superfit.ui.MainActivity

class Service : Service() {

    enum class ServiceState {
        Stopped,
        Starting,
        Started,
        Stopping
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_START -> {
                if (state == ServiceState.Stopped) {
                    state = ServiceState.Starting

                    val channelId =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                createNotificationChannel("my_service", "My Background Service")
                            else
                                // If earlier version channel ID is not used
                                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                                ""

                    val notificationIntent = Intent(this, MainActivity::class.java)
                    notificationIntent.action = ACTION_START
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

                    @Suppress("DEPRECATION")
                    val notification = NotificationCompat.Builder(this, channelId)
                            .setContentTitle("Super Fit")
                            .setContentText("Erfasst Fitness-Daten")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_bike)
                            .setOngoing(true).build()

                    startForeground(NOTIFICATION_ID, notification)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notification)

                    Searcher.start(this)

                    LocationManager.start(this)

                    state = ServiceState.Started
                    return START_STICKY
                }
                return START_NOT_STICKY
            }
            ACTION_STOP -> {
                if (state != ServiceState.Started)
                    return START_NOT_STICKY
                state = ServiceState.Stopping

                LocationManager.stop()

                HeartRate.stop()
                Bike.stop()
                Searcher.stop()

                stopForeground(true)
                stopSelf()

                state = ServiceState.Stopped
                return START_NOT_STICKY
            }

            else -> return START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    companion object {
        var state: ServiceState = ServiceState.Stopped
            set(value){
                field = value
                listener?.invoke(state)
            }

        const val ACTION_START = "Start"
        const val ACTION_STOP = "Stop"
        const val NOTIFICATION_ID = 22

        fun setOnStateChangedListener(listener: ((state: ServiceState)->Unit)?) {
            this.listener = listener
            listener?.invoke(state)
        }

        private var listener: ((state: ServiceState)->Unit)? = null
    }
}
