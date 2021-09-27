package de.uriegel.superfit.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.uriegel.superfit.room.LogEntry
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackPoint
import de.uriegel.superfit.room.TracksRepository
import kotlinx.coroutines.Deferred

class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun findTrackAsync(id: Int): Deferred<Track?> = TracksRepository.findTrackAsync(id)
    fun deleteTrackAsync(id: Int) = TracksRepository.deleteTrackAsync(id)
    fun getAllTracks(): LiveData<Array<Track>>? = allTracks
    fun getEventLog(): LiveData<Array<LogEntry>>? = logEntries
    fun findTrackPointsAsync(trackNr: Int): Deferred<Array<TrackPoint>?> =
        TracksRepository.findTrackPointsAsync(trackNr)

    private val allTracks: LiveData<Array<Track>>? = TracksRepository.allTracks
    private val logEntries: LiveData<Array<LogEntry>>? = TracksRepository.logEntries
}