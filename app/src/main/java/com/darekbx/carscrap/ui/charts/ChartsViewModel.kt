package com.darekbx.carscrap.ui.charts

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.ChartData
import com.darekbx.carscrap.domain.FetchChartDataUseCase
import com.darekbx.carscrap.repository.SynchronizeBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartsViewModel(
    private val fetchChartDataUseCase: FetchChartDataUseCase,
    private val synchronizeBus: SynchronizeBus,
): ViewModel() {

    private val _years = mutableStateOf<List<Int>>(emptyList())
    private val _chartData = mutableStateOf<List<ChartData>>(emptyList())

    val years: State<List<Int>>
        get() = _years

    val chartData: State<List<ChartData>>
        get() = _chartData

    init {
        // TODO
        //listenForChanges()
    }

    private fun listenForChanges(filterId: String) {
        viewModelScope.launch {
            synchronizeBus.listenForTimestamps().collect {
                _chartData.value = emptyList()
                delay(500) // Just for better UX
                withContext(Dispatchers.IO) {
                    // Refresh
                    _chartData.value = fetchChartDataUseCase.fetchChartData(filterId)
                }
            }
        }
    }

    fun fetchYears(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _years.value = fetchChartDataUseCase.fetchYears(filterId)
            }
        }
    }

    fun fetchChartData(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _chartData.value = fetchChartDataUseCase.fetchChartData(filterId)
            }
        }
    }
}
