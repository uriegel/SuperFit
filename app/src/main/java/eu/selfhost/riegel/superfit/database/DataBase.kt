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
                                        (it[TrackTable.Distance] as Double? ?: 0.0).toFloat(),
                                        it[TrackTable.Duration] as Long? ?: 0L,
                                        (it[TrackTable.AverageSpeed] as Double? ?: 0.0).toFloat(),
                                        it[TrackTable.Time] as Long)
                            }.toList().toTypedArray()
                        }
            }
        }
    }

    fun getTrackAsync(trackNr: Long): Deferred<Track> {
        return bg {
            dataBaseHelper.use {
                select(TrackTable.Name,
                        TrackTable.ID,
                        TrackTable.TrackName,
                        TrackTable.Distance,
                        TrackTable.AverageSpeed,
                        TrackTable.Time)
                        .whereArgs("_id = {trackNr}", "trackNr" to trackNr)
                        .exec {
                            return@exec asMapSequence().map {
                                Track((it[TrackTable.ID] as Long).toInt(),
                                        it[TrackTable.TrackName] as String? ?: "",
                                        (it[TrackTable.Distance] as Double? ?: 0.0).toFloat(),
                                        it[TrackTable.Duration] as Long? ?: 0L,
                                        (it[TrackTable.AverageSpeed] as Double? ?: 0.0).toFloat(),
                                        it[TrackTable.Time] as Long)
                            }.first()
                        }
            }
        }
    }

    fun getTrackPointsAsync(trackNr: Long): Deferred<Array<TrackPoint>> {
        return bg {
            dataBaseHelper.use {
                select(TrackPointTable.Name,
                        TrackPointTable.Latitude,
                        TrackPointTable.Longitude,
                        TrackPointTable.Elevation,
                        TrackPointTable.Time,
                        TrackPointTable.Precision,
                        TrackPointTable.Speed,
                        TrackPointTable.HeartRate)
                        .whereArgs("TrackNr = {trackNr}", "trackNr" to trackNr)
                        .exec {
                            return@exec asMapSequence().map {
                                TrackPoint(it[TrackPointTable.Latitude] as Double,
                                        it[TrackPointTable.Longitude] as Double,
                                        (it[TrackPointTable.Elevation] as Double).toFloat(),
                                        it[TrackTable.Time] as Long,
                                        (it[TrackPointTable.Precision] as Double).toFloat(),
                                        (it[TrackPointTable.Speed] as Double).toFloat(),
                                        (it[TrackPointTable.HeartRate] as Long).toInt())
                            }.toList().toTypedArray()
                        }
            }
        }
    }

    fun updateTrack(trackNr: Long, duration: Long, distance: Float, averageSpeed: Float) {
        dataBaseHelper.use {
            update(TrackTable.Name,
                    TrackTable.Distance to distance, TrackTable.AverageSpeed to averageSpeed, TrackTable.Duration to duration)
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