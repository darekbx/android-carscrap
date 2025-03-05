package com.darekbx.carscrap.ui.synchronization

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.repository.remote.RemoteData.SynchronizationStep
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SynchronizeViewModel(private val remoteData: RemoteData) : ViewModel() {

    var synchronizationStep = mutableStateOf<SynchronizationStep?>(null)
    var inProgress = mutableStateOf(false)

    fun synchronize() {
        viewModelScope.launch {
            inProgress.value = true
            delay(500L)
            remoteData.synchronize {
                synchronizationStep.value = this
                if (this == SynchronizationStep.Completed) {
                    delay(500L)
                    inProgress.value = false
                }
            }
        }
    }

    fun reset() {
        synchronizationStep.value = null
    }

    suspend fun lastFetchDateTime(): String? {
        return remoteData.lastFetchDateTime()
    }
}
