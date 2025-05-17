package com.example.railfalldetector.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.railfalldetector.R
import com.example.railfalldetector.data.model.FaultEvent
import com.example.railfalldetector.databinding.FragmentMapBinding
import com.example.railfalldetector.location.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        observeLocationUpdates()
        observeFaultEvents()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun observeLocationUpdates() {
        lifecycleScope.launch {
            LocationService.locationFlow.collect { loc ->
                val pos = LatLng(loc.lat, loc.lon)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
            }
        }
    }

    private fun observeFaultEvents() {
        lifecycleScope.launch {
            FaultEventManager.faultFlow.collect { fe: FaultEvent ->
                val latLng = LatLng(fe.latitude, fe.longitude)
                map?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(fe.type)
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1002
    }
}
