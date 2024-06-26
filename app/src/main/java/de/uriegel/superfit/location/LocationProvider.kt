package de.uriegel.superfit.location

import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TracksRepository
import de.uriegel.superfit.sensor.BikeSensor
import de.uriegel.superfit.sensor.HeartRateSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong

abstract class LocationProvider: CoroutineScope {

    abstract fun start(context: Context)

    open fun stop() {
        onStop()
        gpsActive.postValue(false)
        trackNr?.let {
            // TODO: Only, when enough track points (> 30) otherwise delete track, getTrackPointsCount
            CoroutineScope(Dispatchers.IO).launch {
                TracksRepository.updateTrackAsync(it, BikeSensor.distance, BikeSensor.averageVelocity, BikeSensor.duration).await()
            }
            trackNr = null
        }
    }

    protected fun reset() { trackLine = TrackLine() }

    protected fun onLocationChanged(location: Location) {
        launch {
            if (gpsActive.value == false) {
                gpsActive.value = true
                trackNr = TracksRepository.insertTrackAsync(
                    Track(
                        location.time,
                        location.latitude, location.longitude)
                ).await().toInt()
            }
            // TODO Save and Delete in viewModel
            // TODO Display log details Master/Detail-view
            // TODO optional accuracy circle on location marker
            trackNr?.let { nr ->
                TracksRepository.insertTrackPointAsync(
                    TrackPoint(
                        nr,
                        location.latitude,
                        location.longitude,
                        location.altitude.toFloat(),
                        location.time,
                        location.accuracy
                    )
                        .also {
                            it.heartRate = HeartRateSensor.heartRate.value
                            it.speed = BikeSensor.velocity / 3.6F // in m/s
                        })
                    .await()
            }
            currentPosition.value = LatLong(location.latitude, location.longitude)
            trackLine.addPoint(currentPosition.value)
        }
    }

    protected abstract fun onStop()

    override val coroutineContext = Dispatchers.Main

    companion object {
        val gpsActive: MutableLiveData<Boolean> = MutableLiveData(false)
        var trackLine = TrackLine()
        var trackNr: Int? = null
            private set

        val locationEmpty = LatLong(0.0, 0.0)
        val currentPosition = mutableStateOf(locationEmpty)
        const val LOCATION_REFRESH_TIME = 1000L
        const val LOCATION_REFRESH_DISTANCE = 0.0F
    }
}