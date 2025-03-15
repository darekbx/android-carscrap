package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao

class DataCountUseCase(private val carModelDao: CarModelDao) {

    suspend fun countData(filterId: String): Int {
        return carModelDao.countData(filterId)
    }
}
