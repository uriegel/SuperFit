package de.uriegel.superfit.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.room.Track
import de.uriegel.superfit.room.TracksRepository

class TracksModel : ViewModel() {
    val tracks: LiveData<Array<Track>> = TracksRepository.allTracks
//    val tracks: LiveData<Array<Track>>
//        get() = TracksRepository.allTracks
}