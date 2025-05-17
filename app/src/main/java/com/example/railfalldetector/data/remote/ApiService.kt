package com.example.railfalldetector.data.remote

import com.example.railfalldetector.data.model.SensorData
import com.example.railfalldetector.data.remote.UploadResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/upload")
    suspend fun uploadData(@Body payload: List<SensorData>): UploadResponse
}

data class UploadResponse(val success: Boolean, val message: String)
