package de.uriegel.superfit.room

import android.app.Application
import androidx.lifecycle.LiveData

class TrackRepository(application: Application) {
    // val searchResults = MutableLiveData<List<Track>>()

    var allTracks: LiveData<List<Track>>?

//    fun insertTrack(track: Track) =
//        coroutineScope.launch(Dispatchers.IO) { asyncInsert(track) }
//
//    fun deleteTrack(name: String) =
//        coroutineScope.launch(Dispatchers.IO) { asyncDelete(name) }
//
//    fun findTrack(name: String) =
//        coroutineScope.launch(Dispatchers.Main) { searchResults.value = findAsync(name).await() }

//    private fun asyncInsert(product: Track) = trackDao?.insertTrack(product)
//    private fun asyncDelete(name: String) = trackDao?.deleteTrack(name)
//    private fun findAsync(name: String): Deferred<List<Track>?> =
//        coroutineScope.async(Dispatchers.IO) {
//            return@async trackDao?.findTrack(name)
//        }

    private var trackDao: TrackDao?
    private var trackPointDao: TrackPointDao?
    //private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        val db : TracksRoom? =
            TracksRoom.getDatabase(application)
        trackDao = db?.trackDao()
        trackPointDao = db?.trackPointDao()
        allTracks = trackDao?.getAllTracks()
    }
}