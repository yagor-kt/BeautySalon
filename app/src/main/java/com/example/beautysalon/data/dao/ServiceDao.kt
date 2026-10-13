package com.example.beautysalon.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.beautysalon.data.entity.Service
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY category, title")
    fun getAll(): Flow<List<Service>>

    @Insert
    suspend fun insert(service: Service): Long

    @Delete
    suspend fun delete(service: Service)
}