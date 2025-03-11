package com.darekbx.carscrap.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.repository.local.dto.Filter

@Database(entities = [CarModel::class, Filter::class], version = 2)
abstract class CacheDatabase : RoomDatabase() {

    abstract fun carModelDao(): CarModelDao

    abstract fun filterDao(): FilterDao

    companion object {
        const val DB_NAME = "cache_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE car_model ADD COLUMN filterId TEXT NOT NULL DEFAULT ''")
                database.execSQL(
                    """
            CREATE TABLE filter (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                make TEXT NOT NULL,
                model TEXT NOT NULL,
                generation TEXT NOT NULL,
                salvage INTEGER NOT NULL
            )"""
                )
            }
        }
    }
}
