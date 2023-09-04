package de.uriegel.superfit.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackPointDao {
    @Insert
    fun insertTrackPoint(track: TrackPoint)

    @Query("SELECT * FROM TrackPoints WHERE trackNr = :trackNr")
    fun findTrackPoints(trackNr: Int): Array<TrackPoint>
}