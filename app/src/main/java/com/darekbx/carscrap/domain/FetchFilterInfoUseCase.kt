package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.FilterInfoDao
import com.darekbx.carscrap.repository.local.dto.FilterInfoModel
import com.darekbx.carscrap.ui.filtering.FilterInfo

class FetchFilterInfoUseCase(private val filterInfoDao: FilterInfoDao) {

    suspend fun getFilterInfo(filterId: String): FilterInfo? {
        val filterInfoModel = filterInfoDao.getFilterInfo(filterId) ?: return null
        return filterInfoModel.toFilterInfo()
    }

    private fun FilterInfoModel.toFilterInfo(): FilterInfo {
        return FilterInfo(
            fuelType = this.fuelType.splitAndFilter(),
            enginePower = this.enginePower.splitAndFilter().map { it.toInt() }.toMutableList(),
            gearbox = this.gearbox.splitAndFilter()
        )
    }

    private fun String.splitAndFilter(): MutableList<String> {
        return this.split(",").filter { it.isNotEmpty() }.toMutableList()
    }
}
