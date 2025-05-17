package com.example.railfalldetector.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LocationService : Service() {

    companion object {
        const val CHANNEL_ID = "LocationServiceChannel"
        const val NOTIFICATION_ID = 2
        const val UPDATE_INTERVAL_MS = 5000L
        const val FASTEST_INTERVAL_MS = 2000L
    }

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var callback: LocationCallback

    private val _locationFlow = MutableSharedFlow<Location>(replay = 0)
    val locationFlow = _locationFlow

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_MS
            fastestInterval = FASTEST_INTERVAL_MS
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    CoroutineScope(Dispatchers.Default).launch {
                        _locationFlow.emit(loc)
                    }
                }
            }
        }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        try {
            fusedClient.requestLocationUpdates(
                locationRequest, callback, mainLooper
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        fusedClient.removeLocationUpdates(callback)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(chan)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rail Fault Detector")
            .setContentText("Lokasyon servisi çalışıyor")
            .setSmallIcon(R.drawable.ic_location)
            .build()
    }
}
