package de.uriegel.superfit.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tracks")
data class Track (
    val time: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var trackName: String? = null
    var averageSpeed: Double? = 0.0
    var distance: Double? = 0.0
    var duration: Long? = 0
}