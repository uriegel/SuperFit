package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {
    @Insert
    fun insertTrack(track: Track): Long

    @Query("UPDATE Tracks SET distance=:distance, averageSpeed=:averageVelocity, duration=:duration WHERE id = :id")
    fun updateTrack(id: Int, distance: Float?, averageVelocity: Float?, duration: Int?)

    @Query("SELECT * FROM Tracks WHERE id = :id")
    fun findTrack(id: Int): Track?

    @Query("DELETE FROM Tracks WHERE id = :id")
    fun deleteTrack(id: Int)

    @Query("SELECT * FROM Tracks")
    fun getAllTracks(): LiveData<Array<Track>>
}