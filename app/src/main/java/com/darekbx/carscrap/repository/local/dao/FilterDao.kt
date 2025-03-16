package com.darekbx.carscrap.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.carscrap.repository.local.dto.Filter

@Dao
interface FilterDao {

    @Insert
    suspend fun insertFilter(filter: Filter)

    @Query("SELECT * FROM filter")
    suspend fun getAllFilters(): List<Filter>

    @Query("SELECT * FROM filter WHERE id = :id")
    suspend fun getFilter(id: String): Filter

    @Query("DELETE FROM filter WHERE id = :id")
    suspend fun deleteFilter(id: String)
}
