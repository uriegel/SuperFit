package eu.selfhost.riegel.superfit.android

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.maps.LocationManager
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import eu.selfhost.riegel.superfit.sensors.Searcher
import eu.selfhost.riegel.superfit.ui.MainActivity

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

                    val notificationIntent = Intent(this, MainActivity::class.java)
                    notificationIntent.action = ACTION_START
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

                    @Suppress("DEPRECATION")
                    val notification = NotificationCompat.Builder(this)
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

    companion object {
        var state: ServiceState = ServiceState.Stopped
            set(value){
                field = value
                listener?.invoke(state)
            }

        const val ACTION_START = "Start"
        const val ACTION_STOP = "Stop"
        const val NOTIFICATION_ID = 22

        fun setOnStateChangedListener(listener: ((state: ServiceState)->Unit)?) { this.listener = listener}

        private var listener: ((state: ServiceState)->Unit)? = null
    }
}
