package com.example.railfalldetector.data.local

import androidx.room.TypeConverter
import java.util.Date

object Converters {
    @TypeConverter fun fromTimestamp(value: Long?) = value?.let { Date(it) }
    @TypeConverter fun dateToTimestamp(date: Date?) = date?.time
}
