package com.darekbx.carscrap.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.carscrap.repository.local.dto.CarModel

@Dao
interface CarModelDao {

    @Query("SELECT COUNT(*) FROM car_model WHERE filterId = :filterId")
    suspend fun countData(filterId: String = ""): Int

    @Query("SELECT DISTINCT year FROM car_model WHERE filterId = :filterId")
    suspend fun fetchYears(filterId: String = ""): List<Int>

    @Query("SELECT * FROM car_model WHERE filterId = :filterId ORDER BY createdAt")
    suspend fun fetch(filterId: String = ""): List<CarModel>

    @Query("SELECT DISTINCT externalId FROM car_model WHERE filterId = :filterId")
    suspend fun fetchIds(filterId: String = ""): List<String>

    @Insert
    suspend fun insertAll(carModels: List<CarModel>)

    @Insert
    fun insertAllSync(carModels: List<CarModel>)

    @Query("DELETE FROM car_model WHERE filterId = :filterId")
    suspend fun deleteAll(filterId: String = "")
}
