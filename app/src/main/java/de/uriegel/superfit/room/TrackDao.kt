package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {
    @Insert
    fun insertTrack(track: Track)

    @Query("SELECT * FROM Tracks WHERE trackName = :name")
    fun findTrack(name: String): Array<Track>

    @Query("DELETE FROM Tracks WHERE trackName = :name")
    fun deleteTrack(name: String)

    @Query("SELECT * FROM Tracks")
    fun getAllTracks(): LiveData<Array<Track>>
}