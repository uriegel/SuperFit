package de.uriegel.superfit.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Tracks")
data class Track (
    @NotNull
    val time: Long = 0,
    @NotNull
    val latitude: Double = 0.0,
    @NotNull
    val longitude: Double = 0.0,
) {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0
    var trackName: String? = null
    var averageSpeed: Double? = 0.0
    var distance: Double? = 0.0
    var duration: Long? = 0
}

