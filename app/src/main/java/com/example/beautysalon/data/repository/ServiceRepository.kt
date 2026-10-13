package com.example.beautysalon.data.repository

import com.example.beautysalon.data.dao.ServiceDao
import com.example.beautysalon.data.entity.Service
import kotlinx.coroutines.flow.Flow

class ServiceRepository(
    private val serviceDao: ServiceDao
) {
    fun getAll(): Flow<List<Service>> = serviceDao.getAll()

    suspend fun insert(service: Service): Long = serviceDao.insert(service)

    suspend fun delete(service: Service) = serviceDao.delete(service)
}