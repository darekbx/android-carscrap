package com.darekbx.carscrap.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.carscrap.repository.local.dto.CarModel

@Dao
interface CarModelDao {

    @Query("SELECT * FROM car_model ORDER BY createdAt")
    suspend fun fetch(): List<CarModel>

    @Insert
    suspend fun insertAll(carModels: List<CarModel>)

    @Query("DELETE FROM car_model")
    suspend fun deleteAll()
}
