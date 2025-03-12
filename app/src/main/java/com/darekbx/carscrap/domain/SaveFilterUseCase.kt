package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.Filter

class SaveFilterUseCase(private val filterDao: FilterDao) {

    suspend fun saveFilter(make: String, model: String, generation: String, salvage: Boolean) {
        filterDao.insertFilter(
            Filter(
                make = make.lowercase(),
                model = model.lowercase(),
                generation = generation,
                salvage = salvage
            )
        )
    }
}
