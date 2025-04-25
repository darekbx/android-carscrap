package com.darekbx.carscrap.ui.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.domain.DeleteCarModelUseCase
import com.darekbx.carscrap.domain.FetchListDataUseCase
import com.darekbx.carscrap.repository.local.dto.CarModel
import kotlinx.coroutines.launch

class ListViewModel(
    val fetchListDataUseCase: FetchListDataUseCase,
    val deleteCarModelUseCase: DeleteCarModelUseCase
): ViewModel() {

    private var filterId: String? = null

    private val _cars = mutableStateOf<List<CarModel>>(emptyList())
    private val _inProgress = mutableStateOf<Boolean>(false)

    val cars: State<List<CarModel>>
        get() = _cars

    val inProgress: State<Boolean>
        get() = _inProgress

    fun fetchListData(filterId: String) {
        this.filterId = filterId
        viewModelScope.launch {
            _inProgress.value = true
            _cars.value = fetchListDataUseCase.fetchData(filterId)
            _inProgress.value = false
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _inProgress.value = true
            deleteCarModelUseCase.delete(id)
            filterId?.let {
                _cars.value = fetchListDataUseCase.fetchData(it)
            }
            _inProgress.value = false
        }
    }
}
