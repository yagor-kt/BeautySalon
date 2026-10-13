package com.example.beautysalon.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MasterLoadUi(
    val master: Master,
    val loaded: Int
)

data class DashboardUiState(
    val appointmentsCount: Int = 0,
    val completedCount: Int = 0,
    val cancelledCount: Int = 0,
    val nearestAppointment: Appointment? = null,
    val masterLoads: List<MasterLoadUi> = emptyList()
)

class DashboardViewModel(
    masterRepository: MasterRepository,
    appointmentRepository: AppointmentRepository
) : ViewModel() {
    private val dayStart = DateUtils.startOfDay()
    private val dayEnd = DateUtils.endOfDay()

    val state = combine(
        appointmentRepository.countByDateAndStatus(dayStart, dayEnd, "PENDING"),
        appointmentRepository.countByDateAndStatus(dayStart, dayEnd, "DONE"),
        appointmentRepository.countByDateAndStatus(dayStart, dayEnd, "CANCELLED"),
        masterRepository.getAll(),
        appointmentRepository.getByDate(dayStart, dayEnd)
    ) { pending, done, cancelled, masters, appointments ->
        val now = System.currentTimeMillis()
        DashboardUiState(
            appointmentsCount = pending + done + cancelled,
            completedCount = done,
            cancelledCount = cancelled,
            nearestAppointment = appointments.firstOrNull {
                it.status == "PENDING" && it.dateTime > now
            },
            masterLoads = masters.map { master ->
                MasterLoadUi(
                    master = master,
                    loaded = appointments.count { it.masterName == master.name }
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    class Factory(
        private val masterRepository: MasterRepository,
        private val appointmentRepository: AppointmentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(DashboardViewModel::class.java))
            return DashboardViewModel(masterRepository, appointmentRepository) as T
        }
    }
}