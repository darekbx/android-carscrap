package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.FilterInfoDao
import com.darekbx.carscrap.repository.local.dto.FilterInfoModel
import com.darekbx.carscrap.ui.filtering.FilterInfo

class SaveFilterInfoUseCase(private val filterInfoDao: FilterInfoDao) {

    suspend fun saveFilterInfo(filterId: String, filterInfo: FilterInfo) {
        val filterInfoModel = FilterInfoModel(
            filterId = filterId,
            fuelType = filterInfo.fuelType.joinToString(","),
            enginePower = filterInfo.enginePower.joinToString(","),
            gearbox = filterInfo.gearbox.joinToString(",")
        )
        filterInfoDao.deleteFilterInfo(filterId)
        filterInfoDao.insertFilterInfo(filterInfoModel)
    }
}