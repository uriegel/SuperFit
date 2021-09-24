package de.uriegel.superfit.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.sensor.BikeService
import de.uriegel.superfit.sensor.HeartRateService

class DisplayModel : ViewModel() {

    val cadence: MutableLiveData<Int> = MutableLiveData(-1)
    val velocity: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val heartRate: MutableLiveData<Int> = MutableLiveData(-1)
    val distance: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val duration: MutableLiveData<Int> = MutableLiveData(-1)
    val averageVelocity: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val maxVelocity: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val gpsActive: MutableLiveData<Boolean> = MutableLiveData()

    init {
        LocationManager.setGpsActive = {
            gpsActive.value = true
            LocationManager.setGpsActive = null
        }
        BikeService.setBikeData = { bikeData ->
            velocity.postValue(bikeData.velocity)
            distance.postValue(bikeData.distance)
            maxVelocity.postValue(bikeData.maxVelocity)
            cadence.postValue(bikeData.crankCyclesPerSecs)
            duration.postValue(bikeData.duration)
            averageVelocity.postValue(bikeData.averageVelocity)
        }
        HeartRateService.setHeartRate = { heartRate.postValue(it) }
    }
}
