package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

class FetchChartDataUseCase(private val carModelDao: CarModelDao) {

    suspend fun fetchYears(): List<Int> {
        return carModelDao
            .fetchYears()
            .filter { ACCEPTED_YEARS.contains(it) }
            .sorted()
    }

    suspend fun fetchChartData(): List<ChartData> {
        return carModelDao
            .fetch()
            .groupBy { it.year }
            .filterKeys { ACCEPTED_YEARS.contains(it) }
            .map { ChartData(it.key, it.value) }
            .sortedBy { it.year }
    }

    companion object {
        val ACCEPTED_YEARS = (2012..2024)
    }
}

data class ChartData(val year: Int, val carModels: List<CarModel>)
