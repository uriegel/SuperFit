package de.uriegel.superfit.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "LogEntrys")
data class LogEntry(
    @NotNull
    var entry: String,
    @NotNull
    var type: Type,
    @NotNull
    var time: Long = 0,
    var exception: String?
) {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0

    enum class Type {
        Info,
        Warning,
        Error
    }
}
