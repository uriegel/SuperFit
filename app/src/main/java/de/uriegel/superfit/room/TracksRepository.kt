package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

object TracksRepository {

    val allTracks: LiveData<Array<Track>>?

    fun insertTrack(track: Track): Long = trackDao.insertTrack(track)
    fun deleteTrackAsync(id: Int) =
        coroutineScope.launch(Dispatchers.IO) { trackDao.deleteTrack(id) }
    fun findTrackAsync(id: Int): Deferred<Track?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackDao.findTrack(id)
        }
    fun insertTrackPoint(trackPoint: TrackPoint) = trackPointDao.insertTrackPoint(trackPoint)
    fun findTrackPointsAsync(trackNr: Int): Deferred<Array<TrackPoint>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackPointDao.findTrackPoints(trackNr)
        }
    private val trackDao: TrackDao
    private val trackPointDao: TrackPointDao
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        val db : TracksRoom = TracksRoom.instance
        trackDao = db.trackDao()
        trackPointDao = db.trackPointDao()
        allTracks = trackDao.getAllTracks()
    }
}