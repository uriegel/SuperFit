package de.uriegel.superfit.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import de.uriegel.superfit.android.logInfo

class FusedLocationProvider : LocationProvider() {

    override fun start(context: Context) {
        locationProvider = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = LOCATION_REFRESH_TIME
        locationRequest.fastestInterval = 500
        // TODO gathering mode when inactive
        //locationRequest.maxWaitTime = 60_000

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        return

        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onStop() {
        locationProvider.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.forEach {
                onLocationChanged(it)
            }
        }
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
}