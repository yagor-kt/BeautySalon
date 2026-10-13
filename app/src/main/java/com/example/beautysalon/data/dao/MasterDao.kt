package com.example.beautysalon.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.beautysalon.data.entity.Master
import kotlinx.coroutines.flow.Flow

@Dao
interface MasterDao {
    @Query("SELECT * FROM masters ORDER BY name")
    fun getAll(): Flow<List<Master>>

    @Insert
    suspend fun insert(master: Master): Long
}