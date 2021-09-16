package de.uriegel.superfit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tracks")
class Track {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = 0

    @ColumnInfo(name = "TrackName")
    var trackName: String? = null

    @ColumnInfo(name = "Time")
    var time: Long? = 0

    @ColumnInfo(name = "AverageSpeed")
    var averageSpeed: Double? = 0.0

    @ColumnInfo(name = "Latitude")
    var latitude: Double? = 0.0

    @ColumnInfo(name = "Longitude")
    var longitude: Double? = 0.0

    @ColumnInfo(name = "Distance")
    var distance: Double? = 0.0

    @ColumnInfo(name = "Duration")
    var duration: Long? = 0

    @ColumnInfo(name = "TimeOffset")
    var timeOffset: Long? = 0
}

//@Entity(tableName = "Tracks")
//data class Track(
//    //    @Ignore
////    val trackNr: Int,
//    @PrimaryKey(autoGenerate = true)
//    @NotNull
//    val id: Int = 0,
//    val name: String? = null,
//    @NotNull
//    val latitude: Double = 0.0,
//    @NotNull
//    val longitude: Double = 0.0,
//    @NotNull
//    val time: Long = 0,
//    @NotNull
//    val timeOffset: Int = 0,
//    val distance: Float? = null,
//    val duration: Long? = null,
//    val averageSpeed: Float? = null
//)