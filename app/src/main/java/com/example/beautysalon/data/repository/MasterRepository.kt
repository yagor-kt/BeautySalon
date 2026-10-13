package com.example.beautysalon.data.repository

import com.example.beautysalon.data.dao.MasterDao
import com.example.beautysalon.data.entity.Master
import kotlinx.coroutines.flow.Flow

class MasterRepository(
    private val masterDao: MasterDao
) {
    fun getAll(): Flow<List<Master>> = masterDao.getAll()

    suspend fun insert(master: Master): Long = masterDao.insert(master)
}