package eu.selfhost.riegel.superfit.database

import android.database.sqlite.SQLiteDatabase
import eu.selfhost.riegel.superfit.android.Application
import org.jetbrains.anko.db.*

class DataBaseHelper : ManagedSQLiteOpenHelper(Application.instance, DataBaseHelper.DB_NAME, null, DataBaseHelper.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TrackTable.Name, true,
                TrackTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                TrackTable.Latitude to REAL,
                TrackTable.Longitude to REAL,
                TrackTable.Duration to INTEGER,
                TrackTable.Distance to REAL,
                TrackTable.Time to INTEGER,
                TrackTable.AverageSpeed to REAL)
        db.createTable(TrackPointTable.Name, true,
                TrackPointTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                TrackPointTable.TrackNr to INTEGER,
                TrackPointTable.Latitude to REAL,
                TrackPointTable.Longitude to REAL,
                TrackPointTable.Elevation to REAL,
                TrackPointTable.Time to INTEGER,
                TrackPointTable.HeartRate to INTEGER,
                TrackPointTable.Speed to REAL,
                TrackPointTable.Precision to REAL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(TrackTable.Name,true)
        db.dropTable(TrackPointTable.Name,true)
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "tracks.db"
        const val DB_VERSION = 1
        val instance by lazy { DataBaseHelper() }
    }
}