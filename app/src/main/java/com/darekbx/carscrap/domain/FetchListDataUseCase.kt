package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dto.CarModel

class FetchListDataUseCase(private val carModelDao: CarModelDao) {

    suspend fun fetchData(filterId: String): List<CarModel> {
        return carModelDao.fetch(filterId)
    }
}
