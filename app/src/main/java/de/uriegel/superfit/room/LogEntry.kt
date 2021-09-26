package de.uriegel.superfit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "LogEntrys")
data class LogEntry(
    @NotNull
    @ColumnInfo(name = "Entry")
    var entry: String,
    @NotNull
    @ColumnInfo(name = "Type")
    var type: Type,
    @NotNull
    @ColumnInfo(name = "Time")
    var time: Long = 0,
    @ColumnInfo(name = "Exception")
    var exception: String?
) {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "Id")
    var id: Int = 0

    enum class Type {
        Info,
        Warning,
        Error
    }
}
