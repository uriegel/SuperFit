package de.uriegel.superfit.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.sensors.Bike
import de.uriegel.superfit.sensors.HeartRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DisplayModel : ViewModel(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

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
        HeartRate.listener = { launch { heartRate.value = it } }
        Bike.listener = { launch {
            cadence.value = it.cadence
            velocity.value = it.speed
            distance.value = it.distance
            duration.value = it.duration
            averageVelocity.value = it.averageSpeed
            maxVelocity.value = it.maxSpeed
        }}
    }
}

