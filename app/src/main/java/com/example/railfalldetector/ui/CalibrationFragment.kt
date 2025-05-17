package com.example.railfalldetector.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.railfalldetector.R
import com.example.railfalldetector.databinding.FragmentCalibrationBinding
import com.example.railfalldetector.sensor.CalibrationManager
import com.example.railfalldetector.sensor.SensorService
import com.example.railfalldetector.data.model.SensorData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CalibrationFragment : Fragment(R.layout.fragment_calibration) {
    private var _binding: FragmentCalibrationBinding? = null
    private val binding get() = _binding!!
    private val sampleList = mutableListOf<SensorData>()
    private var collectJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalibrationBinding.bind(view)
        binding.btnCalibrate.setOnClickListener { startCalibration() }
    }

    private fun startCalibration() {
        sampleList.clear()
        collectJob?.cancel()
        collectJob = lifecycleScope.launch {
            requireContext().startService(Intent(requireContext(), SensorService::class.java))
            SensorService.accelFlow.collect { data ->
                sampleList.add(data)
                if (sampleList.size >= 200) {
                    collectJob?.cancel()
                    applyCalibration()
                }
            }
        }
    }

    private fun applyCalibration() {
        val thresholds = CalibrationManager.calibrate(sampleList)
        binding.tvXRange.text = "X: [${thresholds.xLow.format()}, ${thresholds.xHigh.format()}]"
        binding.tvYRange.text = "Y: [${thresholds.yLow.format()}, ${thresholds.yHigh.format()}]"
        binding.tvZRange.text = "Z: [${thresholds.zLow.format()}, ${thresholds.zHigh.format()}]"
    }

    override fun onDestroyView() {
        collectJob?.cancel()
        requireContext().stopService(Intent(requireContext(), SensorService::class.java))
        _binding = null
        super.onDestroyView()
    }

    private fun Float.format(): String = "%.2f".format(this)
}
