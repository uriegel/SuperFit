package de.uriegel.superfit.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "TrackPoints")
data class TrackPoint(
    @NotNull
    val trackNr: Int = 0,
    @NotNull
    val latitude: Double = 0.0,
    @NotNull
    val longitude: Double = 0.0,
    @NotNull
    val elevation: Float = 0f,
    @NotNull
    val time: Long = 0,
    @NotNull
    val precision: Float = 0f,
) {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0
    var speed: Float? = 0f
    var heartRate: Int? = 0
}