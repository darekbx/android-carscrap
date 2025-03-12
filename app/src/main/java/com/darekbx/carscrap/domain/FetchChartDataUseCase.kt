package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

class FetchChartDataUseCase(private val carModelDao: CarModelDao) {

    suspend fun fetchYears(filterId: String): List<Int> {
        return carModelDao
            .fetchYears(filterId, MIN_ITEMS_COUNT)
            .map { it.year }
            .sorted()
    }

    suspend fun fetchChartData(filterId: String): List<ChartData> {
        return carModelDao
            .fetch(filterId)
            .groupBy { it.year }
            .filterValues { it.size >= MIN_ITEMS_COUNT }
            .map { ChartData(it.key, it.value) }
            .sortedBy { it.year }
    }

    companion object {
        val MIN_ITEMS_COUNT = 5
    }
}

data class ChartData(val year: Int, val carModels: List<CarModel>)
