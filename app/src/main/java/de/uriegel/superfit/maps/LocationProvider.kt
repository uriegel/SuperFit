package de.uriegel.superfit.maps

import android.content.Context
import android.location.Location
import de.uriegel.superfit.android.logInfo
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TracksRepository
import de.uriegel.superfit.sensor.BikeService
import de.uriegel.superfit.sensor.HeartRateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

abstract class LocationProvider: CoroutineScope {

    abstract fun start(context: Context)
    open fun stop() {
        onStop()
        gpsActive = false
        trackNr?.let {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            CoroutineScope(Dispatchers.IO).launch {
                TracksRepository.updateTrackAsync(it, BikeService.distance, BikeService.averageVelocity, BikeService.duration).await() //DataBase.updateTrack(trackNr, Bike.duration, Bike.distance, Bike.averageSpeed)
            }
            trackNr = null
        }
    }

    protected fun onLocationChanged(location: Location) {
        launch {
            if (!gpsActive) {
                gpsActive = true
                setGpsActive?.invoke()
                trackNr = TracksRepository.insertTrackAsync(
                    Track(
                    location.time,
                    location.latitude, location.longitude,
                    TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
                )
                ).await().toInt()
            }
            // TODO Display log details Master/Detail-view
            // TODO optional accuracy circle on location marker
            // TODO time without time zone in database and gpx
            trackNr?.let { nr ->
                TracksRepository.insertTrackPointAsync(
                    TrackPoint(nr,location.latitude, location.longitude, location.altitude.toFloat(), location.time, location.accuracy)
                        .also {
                            it.heartRate = HeartRateService.heartRate
                            it.speed = BikeService.velocity / 3.6F // in m/s
                        }).await()
            }
            listener?.invoke(location)
        }
    }

    protected abstract fun onStop()

    override val coroutineContext = Dispatchers.Main

    companion object {
        fun getCurrentTrack() = trackNr
        var listener: ((location: Location)->Unit)? = null
        var gpsActive = false
        var setGpsActive: (()->Unit)? = null
            set(value) {
                field = value
                if (gpsActive)
                    setGpsActive?.invoke()
            }
        private var trackNr: Int? = null

        const val LOCATION_REFRESH_TIME = 1000L
        const val LOCATION_REFRESH_DISTANCE = 0.0F
    }
}