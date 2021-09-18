package de.uriegel.superfit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Tracks")
data class Track (
    @NotNull
    @ColumnInfo(name = "Time")
    val time: Long = 0,
    @NotNull
    @ColumnInfo(name = "Latitude")
    val latitude: Double = 0.0,
    @NotNull
    @ColumnInfo(name = "Longitude")
    val longitude: Double = 0.0,
    @NotNull
    @ColumnInfo(name = "TimeOffset")
    val timeOffset: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @NotNull
    var id: Int = 0
    @ColumnInfo(name = "TrackName")
    var trackName: String? = null
    @ColumnInfo(name = "AverageSpeed")
    var averageSpeed: Double? = 0.0
    @ColumnInfo(name = "Distance")
    var distance: Double? = 0.0
    @ColumnInfo(name = "Duration")
    var duration: Long? = 0
}

