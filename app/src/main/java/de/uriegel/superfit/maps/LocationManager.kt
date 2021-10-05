package de.uriegel.superfit.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import java.util.*

class LocationManager: LocationProvider() {

    override fun start(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
            return

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