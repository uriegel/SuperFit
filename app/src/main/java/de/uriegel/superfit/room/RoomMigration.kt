package de.uriegel.superfit.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `LogEntrys` (`Entry` TEXT NOT NULL, `Type` INTEGER NOT NULL, `Time` INTEGER NOT NULL, `Exception` TEXT, `Id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
    }
}