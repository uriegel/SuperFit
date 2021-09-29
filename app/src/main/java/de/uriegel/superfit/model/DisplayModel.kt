package de.uriegel.superfit.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.maps.LocationProvider
import de.uriegel.superfit.sensor.BikeService
import de.uriegel.superfit.sensor.HeartRateService

class DisplayModel : ViewModel() {

    val heartRate: MutableLiveData<Int> = HeartRateService.heartRate
    val gpsActive: MutableLiveData<Boolean> = LocationProvider.gpsActive
    val cadence: MutableLiveData<Int> = BikeService.cadence
    val velocity: MutableLiveData<Float> = BikeService.velocityData
    val distance: MutableLiveData<Float> = BikeService.distanceData
    val duration: MutableLiveData<Int> = BikeService.durationData
    val averageVelocity: MutableLiveData<Float> = BikeService.averageVelocityData
    val maxVelocity: MutableLiveData<Float> = BikeService.maxVelocityData
}
