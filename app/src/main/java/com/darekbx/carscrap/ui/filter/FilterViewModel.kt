package com.darekbx.carscrap.ui.filter

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.FetchFiltersUseCase
import com.darekbx.carscrap.domain.SaveFilterUseCase
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.Filter
import com.darekbx.carscrap.repository.remote.scrap.FilterFetch
import com.darekbx.carscrap.repository.remote.scrap.FilterVerification
import com.darekbx.carscrap.repository.remote.scrap.Link
import com.darekbx.carscrap.repository.remote.scrap.ModelGenerations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterViewModel(
    private val filterVerification: FilterVerification,
    private val modelGenerations: ModelGenerations,
    private val filterFetch: FilterFetch,
    private val saveFilterUseCase: SaveFilterUseCase,
    private val fetchFiltersUseCase: FetchFiltersUseCase,
    private val carModelDao: CarModelDao,
    private val filterDao: FilterDao
) : ViewModel() {

    private val _generations = mutableStateOf<List<Link>?>(null)
    private val _inProgress = mutableStateOf(false)
    private val _wasVerfied = mutableStateOf<Boolean?>(null)
    private val _filters = mutableStateOf<List<Filter>?>(null)
    private val _refresh = mutableStateOf(false)

    val generations: State<List<Link>?>
        get() = _generations

    val inProgress: State<Boolean>
        get() = _inProgress

    val wasVerfied: State<Boolean?>
        get() = _wasVerfied

    val filters: State<List<Filter>?>
        get() = _filters

    val refresh: State<Boolean>
        get() = _refresh

    fun scrap(filterId: String, onProgress: (Int, Int) -> Unit, onCompleted: (Int) -> Unit) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                filterFetch.fetch(filterId, onProgress, onCompleted)
                _inProgress.value = false
                _refresh.value = !_refresh.value
            }
        }
    }

    fun fetchFilters() {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                _filters.value = fetchFiltersUseCase.fetchFilters().map { filter ->
                    filter.also {
                        filter.itemsCount = carModelDao.countData(filter.id)
                    }
                }
                _inProgress.value = false
            }
        }
    }

    fun saveFilter(
        make: String,
        model: String,
        generation: String,
        salvage: Boolean,
        onCompleted: () -> Unit = { }
    ) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                saveFilterUseCase.saveFilter(make, model, generation, salvage)
                delay(500) // For better UX
                _inProgress.value = false
                onCompleted()
            }
        }
    }

    fun verify(make: String, model: String) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                val result = filterVerification.verify(make, model)
                if (result) {
                    _generations.value = modelGenerations.fetchGenerations(make, model)
                    _wasVerfied.value = true
                } else {
                    _wasVerfied.value = false
                }
                _inProgress.value = false
            }
        }
    }

    fun updateFilterId(newFilterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                carModelDao.updateFilterId(newFilterId, "")
            }
        }
    }

    fun deleteFilterData(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val count = carModelDao.deleteAll(filterId)
                Log.v("FilterViewModel", "Deleted $count items")
                _refresh.value = !_refresh.value
            }
        }
    }

    fun deleteFilter(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filterDao.deleteFilter(filterId)
                _refresh.value = !_refresh.value
            }
        }
    }
}
