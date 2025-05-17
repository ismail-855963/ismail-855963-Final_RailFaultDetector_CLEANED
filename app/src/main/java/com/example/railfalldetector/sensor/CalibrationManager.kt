package com.example.railfalldetector.sensor

import com.example.railfalldetector.data.model.SensorData
import kotlin.math.sqrt

object CalibrationManager {
    data class Thresholds(
        val xLow: Float, val xHigh: Float,
        val yLow: Float, val yHigh: Float,
        val zLow: Float, val zHigh: Float
    )

    fun calibrate(samples: List<SensorData>): Thresholds {
        fun compute(vals: List<Float>): Pair<Float, Float> {
            val mean = vals.average().toFloat()
            val std = sqrt(vals.map { (it - mean) * (it - mean) }.average().toFloat())
            return Pair(mean - 2 * std, mean + 2 * std)
        }

        val xs = samples.map { it.x }
        val ys = samples.map { it.y }
        val zs = samples.map { it.z }

        val (xLow, xHigh) = compute(xs)
        val (yLow, yHigh) = compute(ys)
        val (zLow, zHigh) = compute(zs)

        return Thresholds(xLow, xHigh, yLow, yHigh, zLow, zHigh)
    }
}
