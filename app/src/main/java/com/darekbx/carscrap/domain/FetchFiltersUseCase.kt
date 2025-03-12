package com.darekbx.carscrap.domain

import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.Filter

class FetchFiltersUseCase(private val filterDao: FilterDao) {

    suspend fun fetchFilters(): List<Filter> = filterDao.getAllFilters()
}
