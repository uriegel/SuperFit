package eu.selfhost.riegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import org.mapsforge.core.model.LatLong

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
            trackNr = -1L
        }
    }

    suspend fun getCurrentTrackAsync() : Array<LatLong> {
        if (trackNr == -1L)
            return arrayOf<LatLong>()
        val res = DataBase.getTrackPointsAsync(trackNr)
        return res.map { LatLong(it.latitude, it.longitude) }.toTypedArray()
    }

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            if (!gpsActive) {
                gpsActive = true
                setGpsActive?.invoke()
                trackNr = DataBase.createTrack(location)
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

    private lateinit var locationManager: LocationManager
    private var trackNr = -1L
    private const val LOCATION_REFRESH_TIME = 500L
    private const val LOCATION_REFRESH_DISTANCE = 0.0F
}

