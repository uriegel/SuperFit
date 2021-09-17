package de.uriegel.superfit.room

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class TrackRepository(application: Application) {

    var allTracks: LiveData<Array<Track>>?

//    fun insertTrack(track: Track) =
//        coroutineScope.launch(Dispatchers.IO) { asyncInsert(track) }
//
//    fun deleteTrack(name: String) =
//        coroutineScope.launch(Dispatchers.IO) { asyncDelete(name) }
//
//    private fun asyncInsert(product: Track) = trackDao?.insertTrack(product)
//    private fun asyncDelete(name: String) = trackDao?.deleteTrack(name)
    fun findTrackAsync(id: Int): Deferred<Track?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackDao?.findTrack(id)
        }
    fun findTrackPointsAsync(trackNr: Int): Deferred<Array<TrackPoint>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async trackPointDao?.findTrackPoints(trackNr)
        }
    private var trackDao: TrackDao?
    private var trackPointDao: TrackPointDao?
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        val db : TracksRoom? =
            TracksRoom.getDatabase(application)
        trackDao = db?.trackDao()
        trackPointDao = db?.trackPointDao()
        allTracks = trackDao?.getAllTracks()
    }
}