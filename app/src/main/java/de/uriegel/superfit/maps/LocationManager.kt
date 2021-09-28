package de.uriegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import java.util.*

@SuppressLint("MissingPermission")
class LocationManager: LocationProvider() {

    override fun start(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
    }

    override fun onStop() {
        locationManager.removeUpdates(locationListener)
    }

    private val locationListener = LocationListener {
        location -> this@LocationManager.onLocationChanged(location)
    }

    private lateinit var locationManager: LocationManager
}