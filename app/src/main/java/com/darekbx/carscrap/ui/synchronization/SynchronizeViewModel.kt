package com.darekbx.carscrap.ui.synchronization

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.repository.remote.RemoteData.SynchronizationStep
import kotlinx.coroutines.launch

class SynchronizeViewModel(private val remoteData: RemoteData): ViewModel() {

    var synchronizationStep = mutableStateOf<SynchronizationStep?>(null)

    fun synchronize() {
        viewModelScope.launch {
            remoteData.synchronize {
                synchronizationStep.value = this
            }
        }
    }
}
