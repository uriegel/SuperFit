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
    var trackNr: Int? = 0
    @ColumnInfo(name = "Latitude")
    var latitude: Double? = 0.0
    @ColumnInfo(name = "Longitude")
    var longitude: Double? = 0.0
    @ColumnInfo(name = "Elevation")
    var elevation: Float? = 0f
    @ColumnInfo(name = "Time")
    var time: Long? = 0
    @ColumnInfo(name = "Precision")
    var precision: Float? = 0f
    @ColumnInfo(name = "Speed")
    var speed: Float? = 0f
    @ColumnInfo(name = "HeartRate")
    var heartRate: Int? = 0
}