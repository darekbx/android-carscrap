package com.darekbx.carscrap.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

@Database(entities = [CarModel::class], version = 1)
abstract class CacheDatabase: RoomDatabase() {

    abstract fun carModelDao(): CarModelDao

    companion object {
        const val DB_NAME = "cache_database"
    }
}
