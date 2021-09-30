@file:Suppress("unused")

package de.uriegel.superfit.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `LogEntrys` (`entry` TEXT NOT NULL, `type` INTEGER NOT NULL, `time` INTEGER NOT NULL, `exception` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
    }
}