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
        if (trackNr != -1) {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            //DataBase.updateTrack(trackNr, Bike.duration, Bike.distance, Bike.averageSpeed)
            trackNr = -1
        }
    }

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            if (!gpsActive) {
                gpsActive = true
                setGpsActive?.invoke()
                trackNr = TracksRepository.insertTrack(Track(0, null, location.time,
                    null, location.latitude, location.longitude, null,
                    null, TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings)).toInt()
            }
            listener?.invoke(location)

            //if (location.hasBearing()) {}
            TracksRepository.insertTrackPoint(TrackPoint(null, trackNr, location.latitude,
                location.longitude, location.altitude.toFloat(), location.time, location.accuracy, null, null))
            // TODO: //  Bike.speed, HeartRate.currentHeartRate)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    }

    private var trackNr = -1
    private lateinit var locationManager: LocationManager
    private const val LOCATION_REFRESH_TIME = 500L
    private const val LOCATION_REFRESH_DISTANCE = 0.0F
}