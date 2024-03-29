package de.uriegel.superfit.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.uriegel.superfit.android.Application

@Database(entities = [(Track::class), (TrackPoint::class)], version = 3)
//@TypeConverters(RoomConverters::class)
abstract class TracksRoom: RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun trackPointDao(): TrackPointDao

    companion object {
        val instance: TracksRoom = getDatabase()

        private fun getDatabase(): TracksRoom {
            synchronized(TracksRoom::class.java) {
                return Room.databaseBuilder(Application.instance.applicationContext, TracksRoom::class.java, "tracks")
                    //.addMigrations(Migration_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}