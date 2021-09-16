package de.uriegel.superfit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "TrackPoints")
//data class TrackPoint(
//    @PrimaryKey(autoGenerate = true)
//    @NotNull
//    val id: Int = 0,
//    val trackNr: Int = 0,
//    val latitude: Double = 0.0,
//    val longitude: Double = 0.0,
//    val elevation: Float = 0f,
//    val time: Int = 0,
//    val precision: Float = 0f,
//    val speed: Float? = null,
//    val heartRate: Int? = null,
//)

@Entity(tableName = "TrackPoints")
class TrackPoint {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = 0
    @ColumnInfo(name = "TrackNr")
    var TrackNr: Int? = 0
    var Latitude: Double? = 0.0
    var Longitude: Double? = 0.0
    var Elevation: Float? = 0f
    var Time: Int? = 0
    var Precision: Float? = 0f
    var Speed: Float? = 0f
    var HeartRate: Int? = 0
}