package de.uriegel.superfit.room

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface LogEntryDao {
    @Insert
    fun insertLogEntry(logEntry: LogEntry)
}