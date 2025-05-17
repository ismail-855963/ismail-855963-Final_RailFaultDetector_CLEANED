package com.example.railfalldetector.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SensorData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float,
    val type: Int
)
