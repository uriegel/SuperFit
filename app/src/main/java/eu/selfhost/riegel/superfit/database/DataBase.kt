package eu.selfhost.riegel.superfit.database

import android.content.ContentValues
import android.location.Location
import org.jetbrains.anko.db.*

object DataBase{
    // TODO: Beim Beenden Track aktualisieren
    // TODO: Trackpoints einfügen
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

    fun getTracks() {
        dataBaseHelper.use {
            select(TrackTable.Name,
                    TrackTable.ID,
                    TrackTable.TrackName,
                    TrackTable.Distance,
                    TrackTable.AverageSpeed,
                    TrackTable.Time)
                    .orderBy(TrackTable.Time, SqlOrderDirection.DESC)
                    .exec {
                        val gut = asMapSequence().map{
                            Track((it[TrackTable.ID] as Long).toInt(),
                                    it[TrackTable.TrackName] as String? ?: "",
                                    it[TrackTable.Distance] as Float? ?: 0F,
                                    it[TrackTable.AverageSpeed] as Float? ?: 0F,
                                    it[TrackTable.Time] as Long)
                        }.toList().toTypedArray()
                        val besser = gut
                    }

        }
    }

    private val dataBaseHelper: DataBaseHelper = DataBaseHelper.instance
}