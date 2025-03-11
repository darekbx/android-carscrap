package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

class FetchChartDataUseCase(private val carModelDao: CarModelDao) {

    suspend fun fetchYears(): List<Int> {
        return carModelDao
            .fetchYears("cc8a015d-b871-4420-8dda-6fd146922ec6")
            .filter { ACCEPTED_YEARS.contains(it) }
            .sorted()
    }

    suspend fun fetchChartData(): List<ChartData> {
        return carModelDao
            .fetch("cc8a015d-b871-4420-8dda-6fd146922ec6")
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
