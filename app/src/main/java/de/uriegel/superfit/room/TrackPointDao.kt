package de.uriegel.superfit.room

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TrackPointDao {
    @Insert
    fun insertTrackPoint(track: TrackPoint)
}