package de.uriegel.superfit.android

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import de.uriegel.superfit.R
import de.uriegel.superfit.location.LocationManager
import de.uriegel.superfit.location.LocationProvider
import de.uriegel.superfit.sensor.HeartRateSensor
import de.uriegel.superfit.ui.MainActivity
import de.uriegel.superfit.ui.MainActivity.Companion.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Service: Service() {
    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        notification = NotificationCompat.Builder(this, Application.CHANNEL_SERVICE_ID)
            .setContentTitle(getString(R.string.app_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(R.drawable.bike)
            .setContentIntent(pendingIntent)
            .build()

        locationProvider = LocationManager()
        locationProvider.start(this)

        CoroutineScope(Dispatchers.Default).launch {
            (dataStore.data.first()[MainActivity.prefHeartBeat] ?: false)
                .let {
                    if (it)
                        dataStore.data.first()[MainActivity.prefHeartSensor]
                    else
                        null
                }
                ?.let {
                    if (HeartRateSensor.initialize(this@Service))
                        HeartRateSensor.connect(this@Service, it.split('|').last())
                }
        }
//        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
//        if (BikeService.initialize(this, preferences))
//            BikeService.connect(this)
//        if (HeartRateService.initialize(this, preferences))
//            HeartRateService.connect(this)

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager)
            .run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(20 * 3600_000.toLong())
                }
            }

        running.value = true
        pending.value = false
    }

    override fun onDestroy() {
        super.onDestroy()

        wakeLock.let {
            if (it.isHeld) {
                it.release()
            }
        }

        locationProvider.stop()
        HeartRateSensor.stop()
        //BikeService.stop()

        running.value = false
        pending.value = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    private lateinit var notification: Notification
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var locationProvider: LocationProvider

    companion object {
        val pending = mutableStateOf(false)
        val running = mutableStateOf(false)
    }
}