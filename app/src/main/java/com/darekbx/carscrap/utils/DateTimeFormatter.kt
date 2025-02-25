package com.darekbx.carscrap.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTimeFormatter {

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
