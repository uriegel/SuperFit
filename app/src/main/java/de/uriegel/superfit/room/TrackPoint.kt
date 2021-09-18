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
data class TrackPoint(
    @ColumnInfo(name = "TrackNr")
    val trackNr: Int? = 0,
    @ColumnInfo(name = "Latitude")
    val latitude: Double? = 0.0,
    @ColumnInfo(name = "Longitude")
    val longitude: Double? = 0.0,
    @ColumnInfo(name = "Elevation")
    val elevation: Float? = 0f,
    @ColumnInfo(name = "Time")
    val time: Long? = 0,
    @ColumnInfo(name = "Precision")
    val precision: Float? = 0f,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null
    @ColumnInfo(name = "Speed")
    var speed: Float? = 0f
    @ColumnInfo(name = "HeartRate")
    var heartRate: Int? = 0
}