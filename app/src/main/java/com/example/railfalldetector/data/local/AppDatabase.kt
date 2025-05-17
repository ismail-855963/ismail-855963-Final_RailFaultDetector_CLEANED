package com.example.railfalldetector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.railfalldetector.data.local.Converters
import com.example.railfalldetector.data.model.SensorData
import com.example.railfalldetector.data.model.Profile
import com.example.railfalldetector.data.model.FaultEvent

@Database(
    entities = [SensorData::class, Profile::class, FaultEvent::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensorDataDao(): SensorDataDao
    abstract fun profileDao(): ProfileDao
    abstract fun faultEventDao(): FaultEventDao
}
