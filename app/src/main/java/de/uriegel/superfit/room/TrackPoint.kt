package de.uriegel.superfit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

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
    @NotNull
    val trackNr: Int = 0,
    @ColumnInfo(name = "Latitude")
    @NotNull
    val latitude: Double = 0.0,
    @ColumnInfo(name = "Longitude")
    @NotNull
    val longitude: Double = 0.0,
    @ColumnInfo(name = "Elevation")
    @NotNull
    val elevation: Float = 0f,
    @ColumnInfo(name = "Time")
    @NotNull
    val time: Long = 0,
    @ColumnInfo(name = "Precision")
    @NotNull
    val precision: Float = 0f,
) {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "_id")
    var id: Int = 0
    @ColumnInfo(name = "Speed")
    var speed: Float? = 0f
    @ColumnInfo(name = "HeartRate")
    var heartRate: Int? = 0
}