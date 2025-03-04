package com.darekbx.carscrap.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SynchronizeBus {

    private val _timestampFlow = MutableSharedFlow<Long>()
    val timestampFlow = _timestampFlow.asSharedFlow()

    suspend fun publishTimestamp() {
        _timestampFlow.emit(System.currentTimeMillis())
    }

    fun listenForTimestamps(): Flow<Long> {
        return timestampFlow
    }
}
