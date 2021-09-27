package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

object TracksRepository {

    val allTracks: LiveData<Array<Track>>?
    val logEntries: LiveData<Array<LogEntry>>?

    fun insertTrackAsync(track: Track): Deferred<Long> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackDao.insertTrack(track)
        }
    fun updateTrackAsync(id: Int, distance: Float?, averageVelocity: Float?, duration: Int?): Deferred<Unit> =
        coroutineScope.async(Dispatchers.IO) { return@async trackDao.updateTrack(id, distance, averageVelocity, duration) }
    fun deleteTrackAsync(id: Int) =
        coroutineScope.launch(Dispatchers.IO) { trackDao.deleteTrack(id) }
    fun findTrackAsync(id: Int): Deferred<Track?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackDao.findTrack(id)
        }
    fun insertTrackPointAsync(trackPoint: TrackPoint): Deferred<Unit> =
        coroutineScope.async(Dispatchers.IO) { return@async trackPointDao.insertTrackPoint(trackPoint) }
    fun findTrackPointsAsync(trackNr: Int): Deferred<Array<TrackPoint>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackPointDao.findTrackPoints(trackNr)
        }
    fun insertLogEntryAsync(entry: LogEntry): Deferred<Unit> =
        coroutineScope.async(Dispatchers.IO) { return@async logEntryDao.insertLogEntry(entry) }
    fun clearEventlogAsync(): Deferred<Unit> =
        coroutineScope.async(Dispatchers.IO) { return@async logEntryDao.clearEventlog() }
    private val trackDao: TrackDao
    private val trackPointDao: TrackPointDao
    private val logEntryDao: LogEntryDao
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        val db : TracksRoom = TracksRoom.instance
        trackDao = db.trackDao()
        trackPointDao = db.trackPointDao()
        logEntryDao = db.logEntryDao()
        allTracks = trackDao.getAllTracks()
        logEntries = logEntryDao.getEventlog()
    }
}