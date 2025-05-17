package com.example.railfalldetector.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val xLow: Float,
    val xHigh: Float,
    val yLow: Float,
    val yHigh: Float,
    val zLow: Float,
    val zHigh: Float
)
