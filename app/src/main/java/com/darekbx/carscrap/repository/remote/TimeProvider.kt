package com.darekbx.carscrap.repository.remote

class TimeProvider {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}