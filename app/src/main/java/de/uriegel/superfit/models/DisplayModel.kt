package de.uriegel.superfit.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.maps.LocationManager

class DisplayModel : ViewModel() {

    val cadence: MutableLiveData<String> = MutableLiveData("87")
    val velocity: MutableLiveData<String> = MutableLiveData("29.4")
    val heartRate: MutableLiveData<String> = MutableLiveData("124")
    val distance: MutableLiveData<String> = MutableLiveData("12.5")
    val duration: MutableLiveData<String> = MutableLiveData("1:03:45")
    val averageVelocity: MutableLiveData<String> = MutableLiveData("20.2")
    val maxVelocity: MutableLiveData<String> = MutableLiveData("46.9")
    val gpsActive: MutableLiveData<Boolean> = MutableLiveData()

    init {
        LocationManager.setGpsActive = {
            gpsActive.value = true
            LocationManager.setGpsActive = null
        }
    }
}

