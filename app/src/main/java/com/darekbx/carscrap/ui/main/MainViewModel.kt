package com.darekbx.carscrap.ui.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.DataCountUseCase
import com.darekbx.carscrap.repository.SynchronizeBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val countUseCase: DataCountUseCase,
    private val synchronizeBus: SynchronizeBus
) : ViewModel() {

    private val _count = mutableStateOf(0)

    val count: State<Int>
        get() = _count

    init {
        listenForChanges(filterId = "")
    }

    private fun listenForChanges(filterId: String) {
        viewModelScope.launch {
            synchronizeBus.listenForTimestamps().collect {
                withContext(Dispatchers.IO) {
                    // Refresh
                    _count.value = countUseCase.countData(filterId)
                }
            }
        }
    }

    fun fetchCount(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _count.value = countUseCase.countData(filterId)
            }
        }
    }
}