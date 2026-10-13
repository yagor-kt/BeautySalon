package com.example.beautysalon.data.repository

import com.example.beautysalon.data.MasterCount
import com.example.beautysalon.data.ServiceCount
import com.example.beautysalon.data.dao.AppointmentDao
import com.example.beautysalon.data.entity.Appointment
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(
    private val appointmentDao: AppointmentDao
) {
    fun getByDate(start: Long, end: Long): Flow<List<Appointment>> =
        appointmentDao.getByDate(start, end)

    fun getHistory(): Flow<List<Appointment>> =
        appointmentDao.getHistory()

    suspend fun insert(appointment: Appointment): Long =
        appointmentDao.insert(appointment)

    suspend fun updateStatus(id: Long, status: String) =
        appointmentDao.updateStatus(id, status)

    suspend fun delete(appointment: Appointment) =
        appointmentDao.delete(appointment)

    fun countByDateAndStatus(start: Long, end: Long, status: String): Flow<Int> =
        appointmentDao.countByDateAndStatus(start, end, status)

    fun countByMasterAndDate(masterName: String, start: Long, end: Long): Flow<Int> =
        appointmentDao.countByMasterAndDate(masterName, start, end)

    fun revenueBetween(start: Long, end: Long): Flow<Int?> =
        appointmentDao.revenueBetween(start, end)

    fun topServices(start: Long, end: Long): Flow<List<ServiceCount>> =
        appointmentDao.topServices(start, end)

    fun topMasters(start: Long, end: Long): Flow<List<MasterCount>> =
        appointmentDao.topMasters(start, end)

    fun cancelledBetween(start: Long, end: Long): Flow<Int> =
        appointmentDao.cancelledBetween(start, end)
}