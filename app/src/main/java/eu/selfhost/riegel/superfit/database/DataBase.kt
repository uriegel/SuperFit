package eu.selfhost.riegel.superfit.database

import android.location.Location
import kotlinx.coroutines.Deferred
import org.jetbrains.anko.db.*

object DataBase {
    fun createTrack(location: Location): Long {
        var result = 0L
        database.use {
            result = insert(TrackTable.Name,
                    TrackTable.Longitude to location.longitude,
                    TrackTable.Latitude to location.latitude,
                    TrackTable.Time to location.time)
        }
        return result
    }

    suspend fun getTracks(): Array<Track> {
        return database.use {
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

    fun getTrack(trackNr: Long): Track {
        return database.use {
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

    fun deleteTrack(trackNr: Long): Boolean {
        database.use {
            delete(TrackTable.Name, "_id = {trackNr}", "trackNr" to trackNr)
            delete(TrackPointTable.Name, "TrackNr = {trackNr}", "trackNr" to trackNr)
        }
        return true
    }

    fun getTrackPoints(trackNr: Long): Array<TrackPoint> {
        return database.use {
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

//    // TODO: When < 30 then delete all trackpoints and track with trackNR
//    fun getTrackPointsCountAsync(trackNr: Long): Deferred<Int> {
//        return bg {
//            dataBaseHelper.use {
//                select(TrackPointTable.Name)
//                        .whereArgs("TrackNr = {trackNr}", "trackNr" to trackNr)
//                        .exec {
//                            return@exec asSequence().count()
//                        }
//            }
//        }
//    }

    fun updateTrack(trackNr: Long, duration: Long, distance: Float, averageSpeed: Float) {
        database.use {
            update(TrackTable.Name,
                    TrackTable.Distance to distance, TrackTable.AverageSpeed to averageSpeed, TrackTable.Duration to duration)
                    .whereArgs("_id = {trackNr}", "trackNr" to trackNr)
                    .exec()
        }
    }

    fun insertTrackPoint(trackNr: Long, location: Location, speed: Float, heartRate: Int) {
        database.use {
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

    private val database: DataBaseHelper = DataBaseHelper.instance
}