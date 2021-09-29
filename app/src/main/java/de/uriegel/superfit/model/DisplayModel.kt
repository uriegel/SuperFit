package de.uriegel.superfit.model

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.maps.LocationProvider
import de.uriegel.superfit.sensor.BikeService
import de.uriegel.superfit.sensor.HeartRateService
import de.uriegel.superfit.ui.TrackingFragment

class DisplayModel : ViewModel() {

    val pagingEnabled: MutableLiveData<Boolean> = TrackingFragment.pagingEnabled
    val gpsActive: MutableLiveData<Boolean> = LocationProvider.gpsActive
    val locationData: MutableLiveData<Location> = LocationProvider.locationData
    val heartRate: MutableLiveData<Int> = HeartRateService.heartRate
    val cadence: MutableLiveData<Int> = BikeService.cadence
    val velocity: MutableLiveData<Float> = BikeService.velocityData
    val distance: MutableLiveData<Float> = BikeService.distanceData
    val duration: MutableLiveData<Int> = BikeService.durationData
    val averageVelocity: MutableLiveData<Float> = BikeService.averageVelocityData
    val maxVelocity: MutableLiveData<Float> = BikeService.maxVelocityData
}
