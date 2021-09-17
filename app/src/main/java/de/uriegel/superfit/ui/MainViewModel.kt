package de.uriegel.superfit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TrackRepository
import kotlinx.coroutines.Deferred

class MainViewModel(application: Application) : AndroidViewModel(application) {
//    fun insertProduct(product: Track) = repository.insertTrack(product)
    fun findTrackAsync(id: Int): Deferred<Track?> = repository.findTrackAsync(id)
    fun deleteTrackAsync(id: Int) = repository.deleteTrackAsync(id)
    fun getAllTracks(): LiveData<Array<Track>>? = allTracks
    fun findTrackPointsAsync(trackNr: Int): Deferred<Array<TrackPoint>?> =
        repository.findTrackPointsAsync(trackNr)

    private val repository: TrackRepository = TrackRepository(application)
    private val allTracks: LiveData<Array<Track>>? = repository.allTracks
}