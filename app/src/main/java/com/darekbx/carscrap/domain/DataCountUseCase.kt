package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao

class DataCountUseCase(private val carModelDao: CarModelDao) {

    suspend fun countData(): Int {
        return carModelDao.countData()
    }
}
