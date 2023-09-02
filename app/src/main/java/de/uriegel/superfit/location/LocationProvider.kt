package de.uriegel.superfit.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class LocationProvider: CoroutineScope {

    abstract fun start(context: Context)

    open fun stop() {
        onStop()
        gpsActive.postValue(false)
        trackNr?.let {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            CoroutineScope(Dispatchers.IO).launch {
//                TracksRepository.updateTrackAsync(it, BikeService.distance, BikeService.averageVelocity, BikeService.duration).await() //DataBase.updateTrack(trackNr, Bike.duration, Bike.distance, Bike.averageSpeed)
            }
            trackNr = null
        }
    }

    protected fun onLocationChanged(location: Location) {
        launch {
            if (gpsActive.value == false) {
                gpsActive.value = true
//                trackNr = TracksRepository.insertTrackAsync(Track(
//                    location.time,
//                    location.latitude, location.longitude)
                //).await().toInt()
            }
            // TODO Save and Delete in viewModel
            // TODO Display log details Master/Detail-view
            // TODO optional accuracy circle on location marker
            trackNr?.let { nr ->
//                TracksRepository.insertTrackPointAsync(
//                    TrackPoint(
//                        nr,
//                        location.latitude,
//                        location.longitude,
//                        location.altitude.toFloat(),
//                        location.time,
//                        location.accuracy
//                    )
//                        .also {
//                            it.heartRate = HeartRateService.heartRate.value
//                            it.speed = BikeService.velocity / 3.6F // in m/s
//                        }).await()
            }
            locationData.value = location
        }
    }

    protected abstract fun onStop()

    override val coroutineContext = Dispatchers.Main

    companion object {
        val gpsActive: MutableLiveData<Boolean> = MutableLiveData(false)
        val locationData: MutableLiveData<Location> = MutableLiveData()
        var trackNr: Int? = null
            private set

        const val LOCATION_REFRESH_TIME = 1000L
        const val LOCATION_REFRESH_DISTANCE = 0.0F
    }
}