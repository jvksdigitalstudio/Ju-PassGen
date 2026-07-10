package com.ju.passgen.data

import java.text.SimpleDateFormat
import java.util.*

data class HistoryEntry(
    val password: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val timeLabel: String
        get() {
            val fmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return fmt.format(Date(timestamp))
        }
}
