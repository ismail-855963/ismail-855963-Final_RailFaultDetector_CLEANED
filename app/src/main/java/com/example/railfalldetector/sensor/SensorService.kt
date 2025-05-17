package com.example.railfalldetector.sensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SensorService : Service(), SensorEventListener {

    companion object {
        const val CHANNEL_ID = "SensorServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val _sensorFlow = MutableSharedFlow<SensorData>(replay = 0)
    val sensorFlow = _sensorFlow

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerListener()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterListener()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerListener() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val data = SensorData(
                timestamp = System.currentTimeMillis(),
                x = it.values[0],
                y = it.values[1],
                z = it.values[2]
            )
            CoroutineScope(Dispatchers.Default).launch {
                _sensorFlow.emit(data)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sensor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rail Fault Detector")
            .setContentText("Sensör servisi çalışıyor")
            .setSmallIcon(R.drawable.ic_sensor)
            .build()
    }
}
