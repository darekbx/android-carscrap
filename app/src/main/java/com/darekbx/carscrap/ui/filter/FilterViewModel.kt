package com.darekbx.carscrap.ui.filter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.FetchFiltersUseCase
import com.darekbx.carscrap.domain.SaveFilterUseCase
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
    private val fetchFiltersUseCase: FetchFiltersUseCase
) : ViewModel() {

    private val _generations = mutableStateOf<List<Link>?>(null)
    private val _inProgress = mutableStateOf(false)
    private val _wasVerfied = mutableStateOf<Boolean?>(null)
    private val _filters = mutableStateOf<List<Filter>?>(null)

    val generations: State<List<Link>?>
        get() = _generations

    val inProgress: State<Boolean>
        get() = _inProgress

    val wasVerfied: State<Boolean?>
        get() = _wasVerfied

    val filters: State<List<Filter>?>
        get() = _filters

    fun scrap(filterId: String) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                filterFetch.fetch(filterId)
                _inProgress.value = false
            }
        }
    }

    fun fetchFilters() {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                _filters.value = fetchFiltersUseCase.fetchFilters()
                _inProgress.value = false
            }
        }
    }

    fun saveFilter(make: String, model: String, generation: String, salvage: Boolean) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                saveFilterUseCase.saveFilter(make, model, generation, salvage)
                delay(500) // For better UX
                _inProgress.value = false
            }
        }
    }

    fun verify(make: String, model: String) {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                val result  = filterVerification.verify(make, model)
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
}
