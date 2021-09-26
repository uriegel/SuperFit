package de.uriegel.superfit.room

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun toType(value: Int) = enumValues<LogEntry.Type>()[value]

    @TypeConverter
    fun fromType(value: LogEntry.Type) = value.ordinal
}