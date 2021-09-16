package de.uriegel.superfit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TrackRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun insertProduct(product: Track) = repository.insertTrack(product)
    fun findProduct(name: String) = repository.findTrack(name)
    fun deleteProduct(name: String) = repository.deleteTrack(name)
    fun getSearchResults(): MutableLiveData<List<Track>> = searchResults
    fun getAllTracks(): LiveData<List<Track>>? = allTracks

    private val repository: TrackRepository = TrackRepository(application)
    private val allTracks: LiveData<List<Track>>? = repository.allTracks
    private val searchResults: MutableLiveData<List<Track>> = repository.searchResults
}