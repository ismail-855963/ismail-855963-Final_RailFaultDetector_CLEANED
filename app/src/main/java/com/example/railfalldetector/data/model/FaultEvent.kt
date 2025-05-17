package com.example.railfalldetector.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "fault_events")
data class FaultEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "timestamp")
    val timestamp: Date,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "severity")
    val severity: Float,
    @ColumnInfo(name = "description")
    val description: String? = null
)
