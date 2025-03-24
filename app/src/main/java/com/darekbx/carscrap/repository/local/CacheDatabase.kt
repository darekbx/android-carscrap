package com.darekbx.carscrap.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dao.FilterInfoDao
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.repository.local.dto.Filter
import com.darekbx.carscrap.repository.local.dto.FilterInfoModel

@Database(entities = [CarModel::class, Filter::class, FilterInfoModel::class], version = 4)
abstract class CacheDatabase : RoomDatabase() {

    abstract fun carModelDao(): CarModelDao

    abstract fun filterDao(): FilterDao

    abstract fun filterInfoDao(): FilterInfoDao

    companion object {
        const val DB_NAME = "cache_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE car_model ADD COLUMN filterId TEXT NOT NULL DEFAULT ''")
                database.execSQL(
                    """
            CREATE TABLE filter (
                id TEXT PRIMARY KEY NOT NULL,
                make TEXT NOT NULL,
                model TEXT NOT NULL,
                generation TEXT NOT NULL,
                salvage INTEGER NOT NULL
            )"""
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE filter_info (
                        id TEXT PRIMARY KEY NOT NULL,
                        fuelType TEXT NOT NULL,
                        enginePower TEXT NOT NULL,
                        gearbox TEXT NOT NULL
                    )
                    """
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE filter_info ADD COLUMN filterId TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
