package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

class FetchChartDataUseCase(private val carModelDao: CarModelDao) {

    suspend fun fetchYears(): List<Int> {
        return carModelDao.fetchYears().sorted()
    }

    suspend fun fetchChartData(): List<ChartData> {
        return carModelDao
            .fetch()
            .groupBy { it.year }
            .map { ChartData(it.key, it.value) }
            .sortedBy { it.year }
    }
}

data class ChartData(val year: Int, val carModels: List<CarModel>)
