package com.example.beautysalon.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.beautysalon.data.entity.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query(
        """
        SELECT * FROM appointments
        WHERE dateTime BETWEEN :start AND :end
        ORDER BY dateTime ASC
        """
    )
    fun getByDate(start: Long, end: Long): Flow<List<Appointment>>

    @Query(
        """
        SELECT * FROM appointments
        WHERE masterName = :masterName AND dateTime >= :after
        ORDER BY dateTime ASC
        LIMIT 3
        """
    )
    fun getUpcomingByMaster(masterName: String, after: Long): Flow<List<Appointment>>

    @Query(
        """
        SELECT * FROM appointments
        WHERE status != 'PENDING'
        ORDER BY dateTime DESC
        """
    )
    fun getHistory(): Flow<List<Appointment>>

    @Insert
    suspend fun insert(appointment: Appointment): Long

    @Query("UPDATE appointments SET status = :status WHERE id = :id AND status = 'PENDING'")
    suspend fun updateStatus(id: Long, status: String)

    @Delete
    suspend fun delete(appointment: Appointment)

    @Query(
        """
        SELECT COUNT(*) FROM appointments
        WHERE dateTime BETWEEN :start AND :end AND status = :status
        """
    )
    fun countByDateAndStatus(start: Long, end: Long, status: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM appointments
        WHERE masterName = :masterName AND dateTime BETWEEN :start AND :end
        """
    )
    fun countByMasterAndDate(masterName: String, start: Long, end: Long): Flow<Int>

    @Query(
        """
        SELECT SUM(price) FROM appointments
        WHERE status = 'DONE' AND dateTime BETWEEN :start AND :end
        """
    )
    fun revenueBetween(start: Long, end: Long): Flow<Int?>

    @Query(
        """
        SELECT serviceTitle AS title, COUNT(*) AS count
        FROM appointments
        WHERE status = 'DONE' AND dateTime BETWEEN :start AND :end
        GROUP BY serviceTitle
        ORDER BY count DESC
        LIMIT 3
        """
    )
    fun topServices(start: Long, end: Long): Flow<List<ServiceCount>>

    @Query(
        """
        SELECT masterName AS name, COUNT(*) AS count
        FROM appointments
        WHERE status = 'DONE' AND dateTime BETWEEN :start AND :end
        GROUP BY masterName
        ORDER BY count DESC
        LIMIT 3
        """
    )
    fun topMasters(start: Long, end: Long): Flow<List<MasterCount>>

    @Query(
        """
        SELECT COUNT(*) FROM appointments
        WHERE status = 'CANCELLED' AND dateTime BETWEEN :start AND :end
        """
    )
    fun cancelledBetween(start: Long, end: Long): Flow<Int>
}