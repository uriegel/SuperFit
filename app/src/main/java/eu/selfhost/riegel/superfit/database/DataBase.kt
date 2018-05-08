package eu.selfhost.riegel.superfit.database

import android.location.Location
import kotlinx.coroutines.experimental.Deferred
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.db.*

object DataBase {
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

    fun getTracksAsync(): Deferred<Array<Track>> {
        return bg {
            dataBaseHelper.use {
                select(TrackTable.Name,
                        TrackTable.ID,
                        TrackTable.TrackName,
                        TrackTable.Distance,
                        TrackTable.AverageSpeed,
                        TrackTable.Time)
                        .orderBy(TrackTable.Time, SqlOrderDirection.DESC)
                        .exec {
                            return@exec asMapSequence().map {
                                Track((it[TrackTable.ID] as Long).toInt(),
                                        it[TrackTable.TrackName] as String? ?: "",
                                        it[TrackTable.Distance] as Float? ?: 0F,
                                        it[TrackTable.AverageSpeed] as Float? ?: 0F,
                                        it[TrackTable.Time] as Long)
                            }.toList().toTypedArray()
                        }
            }
        }
    }

    fun updateTrack(trackNr: Long, distance: Float, averageSpeed: Float) {
        dataBaseHelper.use {
            update(TrackTable.Name,
                    TrackTable.Distance to distance, TrackTable.AverageSpeed to averageSpeed)
                    .whereArgs("_id = {trackNr}", "trackNr" to trackNr)
                    .exec()
        }
    }

    fun insertTrackPoint(trackNr: Long, location: Location, speed: Float, heartRate: Int) {
        dataBaseHelper.use {
            insert(TrackPointTable.Name,
                    TrackPointTable.TrackNr to trackNr,
                    TrackPointTable.Longitude to location.longitude,
                    TrackPointTable.Latitude to location.latitude,
                    TrackPointTable.Elevation to location.altitude,
                    TrackPointTable.Time to location.time,
                    TrackPointTable.Precision to location.accuracy,
                    TrackPointTable.Speed to speed,
                    TrackPointTable.HeartRate to heartRate)
        }
    }

    private val dataBaseHelper: DataBaseHelper = DataBaseHelper.instance
}