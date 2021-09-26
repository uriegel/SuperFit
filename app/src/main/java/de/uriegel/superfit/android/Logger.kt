@file:Suppress("unused")

package de.uriegel.superfit.android

import de.uriegel.superfit.room.LogEntry
import de.uriegel.superfit.room.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

fun logInfo(text: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val now = Calendar.getInstance().time
        TracksRepository.insertLogEntryAsync(LogEntry(text, LogEntry.Type.Info, now.time, null)).await()
    }
}

fun logWarnung(text: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val now = Calendar.getInstance().time
        TracksRepository.insertLogEntryAsync(LogEntry(text, LogEntry.Type.Warning, now.time, null)).await()
    }
}

fun logWarnung(text: String, e: Exception) {
    CoroutineScope(Dispatchers.IO).launch {
        val now = Calendar.getInstance().time
        TracksRepository.insertLogEntryAsync(LogEntry(text, LogEntry.Type.Warning, now.time, e.toString())).await()
    }
}

fun logError(text: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val now = Calendar.getInstance().time
        TracksRepository.insertLogEntryAsync(LogEntry(text, LogEntry.Type.Error, now.time, null)).await()
    }
}

fun logError(text: String, e: Exception) {
    CoroutineScope(Dispatchers.IO).launch {
        val now = Calendar.getInstance().time
        TracksRepository.insertLogEntryAsync(LogEntry(text, LogEntry.Type.Error, now.time, e.toString())).await()
    }
}
