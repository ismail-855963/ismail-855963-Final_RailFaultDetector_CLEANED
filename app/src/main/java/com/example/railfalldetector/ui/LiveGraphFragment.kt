package com.example.railfalldetector.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.railfalldetector.R
import com.example.railfalldetector.databinding.FragmentLiveGraphBinding
import com.example.railfalldetector.sensor.SensorService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LiveGraphFragment : Fragment(R.layout.fragment_live_graph) {
    private var _binding: FragmentLiveGraphBinding? = null
    private val binding get() = _binding!!
    private val xEntries = mutableListOf<Entry>()
    private val yEntries = mutableListOf<Entry>()
    private val zEntries = mutableListOf<Entry>()
    private var index = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLiveGraphBinding.bind(view)
        setupChart()
        startGraphUpdates()
    }

    private fun setupChart() {
        binding.lineChart.description.isEnabled = false
        binding.lineChart.xAxis.setDrawLabels(false)
        binding.lineChart.axisRight.isEnabled = false
    }

    private fun startGraphUpdates() {
        requireContext().startService(Intent(requireContext(), SensorService::class.java))
        lifecycleScope.launch(Dispatchers.Main) {
            SensorService.accelFlow.collect { data ->
                xEntries.add(Entry(index, data.x))
                yEntries.add(Entry(index, data.y))
                zEntries.add(Entry(index, data.z))
                index += 1f
                updateChart()
            }
        }
    }

    private fun updateChart() {
        val xSet = LineDataSet(xEntries, "X").apply { setDrawCircles(false) }
        val ySet = LineDataSet(yEntries, "Y").apply { setDrawCircles(false) }
        val zSet = LineDataSet(zEntries, "Z").apply { setDrawCircles(false) }
        binding.lineChart.data = LineData(xSet, ySet, zSet)
        binding.lineChart.notifyDataSetChanged()
        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        requireContext().stopService(Intent(requireContext(), SensorService::class.java))
        _binding = null
        super.onDestroyView()
    }
}
