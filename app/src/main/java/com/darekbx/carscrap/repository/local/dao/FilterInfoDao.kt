package com.darekbx.carscrap.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.carscrap.repository.local.dto.FilterInfoModel

@Dao
interface FilterInfoDao {

    @Insert
    suspend fun insertFilterInfo(filterInfoModel: FilterInfoModel)

    @Query("DELETE FROM filter_info WHERE filterId = :filterId")
    suspend fun deleteFilterInfo(filterId: String)

    @Query("SELECT * FROM filter_info WHERE filterId = :filterId")
    suspend fun getFilterInfo(filterId: String): FilterInfoModel?
}
