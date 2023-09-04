package de.uriegel.superfit.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TrackPoints")
data class TrackPoint(
    val trackNr: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Float = 0f,
    val time: Long = 0,
    val precision: Float = 0f,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var speed: Float? = 0f
    var heartRate: Int? = 0
}