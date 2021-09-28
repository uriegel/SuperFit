package de.uriegel.superfit.maps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*

class FusedLocationProvider : LocationProvider() {

    @SuppressLint("MissingPermission")
    override fun start(context: Context) {
        locationProvider = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = LOCATION_REFRESH_TIME
        locationRequest.fastestInterval = 500
        locationRequest.maxWaitTime = 60_000

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