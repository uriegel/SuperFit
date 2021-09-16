package de.uriegel.superfit.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Track::class), (TrackPoint::class)], version = 1)
abstract class TracksRoom: RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun trackPointDao(): TrackPointDao

    companion object {
        internal fun getDatabase(context: Context): TracksRoom? {
            if (instance == null) {
                synchronized(TracksRoom::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, TracksRoom::class.java, "tracks.db")
                        .build()
                }
            }
            return instance
        }
        private var instance: TracksRoom? = null
    }
}
