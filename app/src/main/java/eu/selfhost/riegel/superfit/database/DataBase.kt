package eu.selfhost.riegel.superfit.database

import android.content.ContentValues
import android.location.Location
import org.jetbrains.anko.db.insert

object DataBase{
    fun createTrack(location: Location): Long {
        var result = 0L
        dataBaseHelper.use {
            result = insert(TrackTable.Name,
                    TrackTable.Longitude to location.longitude,
                    TrackTable.Latitude to location.latitude,
                    TrackTable.Time to location.time)
        }
        return result
    }

    private val dataBaseHelper: DataBaseHelper = DataBaseHelper.instance
}