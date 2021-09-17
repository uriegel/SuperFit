package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {
    @Insert
    fun insertTrack(track: Track): Long

    @Query("SELECT * FROM Tracks WHERE _id = :id")
    fun findTrack(id: Int): Track?

    @Query("DELETE FROM Tracks WHERE _id = :id")
    fun deleteTrack(id: Int)

    @Query("SELECT * FROM Tracks")
    fun getAllTracks(): LiveData<Array<Track>>
}