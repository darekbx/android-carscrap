package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.EnginePowerCount

class FilteringUseCase(private val carModelDao: CarModelDao) {

    suspend fun getDistinctFuelTypes(filterId: String): List<String> {
        return carModelDao.getDistinctFuelTypes(filterId)
    }

    suspend fun getDistinctEnginePowers(filterId: String): List<EnginePowerCount> {
        return carModelDao.getDistinctEnginePowers(filterId)
            .sortedByDescending { it.count }
            .filter { it.count > MIN_ITEMS_COUNT }
    }

    suspend fun getDistinctGearboxes(filterId: String): List<String> {
        return carModelDao.getDistinctGearboxes(filterId)
    }

    companion object {
        const val MIN_ITEMS_COUNT = 5
    }
}