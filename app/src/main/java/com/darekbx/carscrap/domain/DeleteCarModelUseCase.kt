package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.CarModelDao

class DeleteCarModelUseCase(private val carModelDao: CarModelDao) {

    suspend fun delete(id: String) {
        carModelDao.delete(id)
    }
}
