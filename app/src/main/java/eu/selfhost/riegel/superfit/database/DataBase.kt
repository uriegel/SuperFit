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
            select(TrackTable.Name, TrackTable.Time)
                    .orderBy(TrackTable.Time, SqlOrderDirection.DESC)
                    .exec {
                        val seq = this.asMapSequence()
                        val gut = seq.map({
                            it[TrackTable.Time]
                        })
                        val affe = gut.toList().toTypedArray()
                        for (besser in gut) {
                            var t = besser
                            val tt = t
                        }
                    }

        }
    }

    private val dataBaseHelper: DataBaseHelper = DataBaseHelper.instance
}