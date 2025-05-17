package com.example.railfalldetector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.railfalldetector.data.model.FaultEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface FaultEventDao {
    @Insert
    suspend fun insert(event: FaultEvent): Long

    @Query("SELECT * FROM fault_events ORDER BY timestamp DESC")
    fun getAllFaultEvents(): Flow<List<FaultEvent>>

    @Query("SELECT * FROM fault_events WHERE id = :id")
    suspend fun getById(id: Long): FaultEvent?

    @Delete
    suspend fun delete(event: FaultEvent)

    @Query("DELETE FROM fault_events")
    suspend fun deleteAll()
}
