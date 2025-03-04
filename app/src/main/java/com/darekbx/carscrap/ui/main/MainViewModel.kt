package com.darekbx.carscrap.ui.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.DataCountUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val countUseCase: DataCountUseCase): ViewModel() {

    private val _count = mutableStateOf(0)

    val count: State<Int>
        get() = _count

    fun fetchCount() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _count.value = countUseCase.countData()
            }
        }
    }
}