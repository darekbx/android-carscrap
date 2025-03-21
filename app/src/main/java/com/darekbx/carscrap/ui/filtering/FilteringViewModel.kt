package com.darekbx.carscrap.ui.filtering

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.FilteringUseCase
import com.darekbx.carscrap.repository.local.dto.EnginePowerCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FilterInfo(
    val fuelType: MutableList<String> = mutableListOf(),
    val enginePower: MutableList<Int> = mutableListOf(),
    val gearbox: MutableList<String> = mutableListOf()
) {
    fun addFuelTypes(items: List<String>) {
        fuelType.clear()
        fuelType.addAll(items)
    }

    fun addEnginePowers(items: List<Int>) {
        enginePower.clear()
        enginePower.addAll(items)
    }

    fun addGearboxes(items: List<String>) {
        gearbox.clear()
        gearbox.addAll(items)
    }
}

class FilteringViewModel(private val filteringUseCase: FilteringUseCase) : ViewModel() {

    private val _fuelTypes = mutableStateOf<List<String>>(emptyList())
    val fuelTypes: State<List<String>>
        get() = _fuelTypes

    private val _enginePowers = mutableStateOf<List<EnginePowerCount>>(emptyList())
    val enginePowers: State<List<EnginePowerCount>>
        get() = _enginePowers

    private val _gearboxes = mutableStateOf<List<String>>(emptyList())
    val gearboxes: State<List<String>>
        get() = _gearboxes

    fun fetchFilters(filterId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _fuelTypes.value = filteringUseCase.getDistinctFuelTypes(filterId)
                _enginePowers.value = filteringUseCase.getDistinctEnginePowers(filterId)
                _gearboxes.value = filteringUseCase.getDistinctGearboxes(filterId)
            }
        }
    }
}
