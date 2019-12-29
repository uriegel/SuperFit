package de.uriegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import de.uriegel.superfit.database.DataBase
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import org.mapsforge.core.model.LatLong
import java.util.*

@SuppressLint("MissingPermission")
object LocationManager {

    var listener: ((location: Location)->Unit)? = null

    fun start(context: Context) {
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
    }

    fun stop() {
        locationManager.removeUpdates(locationListener)
        gpsActive = false
        if (trackNr != -1L) {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            DataBase.updateTrack(trackNr, Bike.duration, Bike.distance, Bike.averageSpeed)
            trackNr = -1
        }
    }

    fun getCurrentTrackAsync() : Array<LatLong> {
        if (trackNr == -1L)
            return arrayOf()
        val res = DataBase.getTrackPoints(trackNr)
        return res.map { LatLong(it.latitude, it.longitude) }.toTypedArray()
    }

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            if (!gpsActive) {
                gpsActive = true
                setGpsActive?.invoke()
                trackNr = DataBase.createTrack(location,
                        TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings)
            }
            listener?.invoke(location)

            //if (location.hasBearing()) {
            //}
            DataBase.insertTrackPoint(trackNr, location, Bike.speed, HeartRate.currentHeartRate)
        }

        override fun onProviderEnabled(p0: String?) {}
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderDisabled(p0: String?) {}
    }

    var gpsActive = false
    var setGpsActive: (()->Unit)? = null
        set(value) {
            field = value
            if (gpsActive)
                setGpsActive?.invoke()
        }

    private lateinit var locationManager: LocationManager
    private var trackNr = -1L
    private const val LOCATION_REFRESH_TIME = 500L
    private const val LOCATION_REFRESH_DISTANCE = 0.0F
}

