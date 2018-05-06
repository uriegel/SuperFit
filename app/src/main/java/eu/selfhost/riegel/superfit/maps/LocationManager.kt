package eu.selfhost.riegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.Bundle

@SuppressLint("MissingPermission")
class LocationManager(context: Context) {

    fun stop() {
        locationManager.removeUpdates(locationListener)
//        gpsActive = false
    }

    //fun getTrackNumber(): Long? = trackPoints?.trackNumber

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            if (!gpsActive) {
                gpsActive = true
                setGpsActive?.invoke()
            }
//            setCurrentLocation(location)

//            if (trackPoints == null)
//                trackPoints = dataSource.createTrack(location.longitude, location.latitude, location.time)

            if (location.hasBearing()) {
            }
//            trackPoints!!.add(TrackPoint(location.latitude, location.longitude, location.altitude,
//                    location.time, location.accuracy, data.speed, data.heartRate))
            //        mapView.setCenter(LatLong(location.latitude, location.longitude))
        }

        override fun onProviderEnabled(p0: String?) {}
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderDisabled(p0: String?) {}
    }

    private val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

    companion object {
        var gpsActive = false
        var setGpsActive: (()->Unit)? = null

        private const val LOCATION_REFRESH_TIME = 500L
        private const val LOCATION_REFRESH_DISTANCE = 0.0F
    }
//    private var trackPoints: TrackPointsDataSource? = null

    init {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
    }
}

