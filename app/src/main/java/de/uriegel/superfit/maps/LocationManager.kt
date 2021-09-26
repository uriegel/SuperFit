package de.uriegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TracksRepository
import de.uriegel.superfit.sensor.BikeService
import de.uriegel.superfit.sensor.HeartRateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("MissingPermission")
object LocationManager {
    var listener: ((location: Location)->Unit)? = null
    var gpsActive = false
    var setGpsActive: (()->Unit)? = null
        set(value) {
            field = value
            if (gpsActive)
                setGpsActive?.invoke()
        }

    fun start(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
    }

    fun stop() {
        locationManager.removeUpdates(locationListener)
        gpsActive = false
        trackNr?.let {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            CoroutineScope(Dispatchers.IO).launch {
                TracksRepository.updateTrackAsync(it, BikeService.distance, BikeService.averageVelocity, BikeService.duration).await() //DataBase.updateTrack(trackNr, Bike.duration, Bike.distance, Bike.averageSpeed)
            }
            trackNr = null
        }
    }

    fun getCurrentTrack() = trackNr

    private val locationListener = object : LocationListener, CoroutineScope {

        override fun onLocationChanged(location: Location) {
            launch {

                if (!gpsActive) {
                    gpsActive = true
                    setGpsActive?.invoke()
                    trackNr = TracksRepository.insertTrackAsync(Track(
                            location.time,
                            location.latitude, location.longitude,
                            TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
                        )).await().toInt()
                }
                // TODO clearLog
                // TODO Display log
                // TODO MapFragment and PageViewer as before
                // TODO optional accuracy circle on location marker
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

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

        override val coroutineContext = Dispatchers.Main
    }

    private var trackNr: Int? = null
    private lateinit var locationManager: LocationManager
    private const val LOCATION_REFRESH_TIME = 500L
    private const val LOCATION_REFRESH_DISTANCE = 0.0F
}