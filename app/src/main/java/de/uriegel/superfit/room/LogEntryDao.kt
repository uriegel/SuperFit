package de.uriegel.superfit.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogEntryDao {
    @Insert
    fun insertLogEntry(logEntry: LogEntry)

    @Query("SELECT * FROM LogEntrys")
    fun getEventlog(): LiveData<Array<LogEntry>>

    @Query("DELETE FROM LogEntrys")
    fun clearEventlog()
}