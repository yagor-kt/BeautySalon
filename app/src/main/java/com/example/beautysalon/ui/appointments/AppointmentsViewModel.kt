package com.example.beautysalon.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.entity.Service
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppointmentsUiState(
    val appointments: List<Appointment> = emptyList(),
    val masters: List<Master> = emptyList(),
    val services: List<Service> = emptyList(),
    val selectedMaster: String? = null
)

class AppointmentsViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val masterRepository: MasterRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {
    private val selectedMaster = MutableStateFlow<String?>(null)
    private val dayStart = DateUtils.startOfDay()
    private val dayEnd = DateUtils.endOfDay()

    val state = combine(
        appointmentRepository.getByDate(dayStart, dayEnd),
        masterRepository.getAll(),
        serviceRepository.getAll(),
        selectedMaster
    ) { appointments, masters, services, filter ->
        AppointmentsUiState(
            appointments = if (filter == null) {
                appointments
            } else {
                appointments.filter { it.masterName == filter }
            },
            masters = masters,
            services = services,
            selectedMaster = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppointmentsUiState()
    )

    fun selectMaster(name: String?) {
        selectedMaster.value = name
    }

    /**
     * Возвращает false, если дата или время не прошли строгую проверку.
     * Запись создаётся только после успешного разбора даты и времени.
     */
    fun createAppointment(
        clientName: String,
        service: Service,
        master: Master,
        date: String,
        time: String
    ): Boolean {
        val dateTime = DateUtils.parseDateTime(date, time) ?: return false
        val normalizedClientName = clientName.trim()
        if (normalizedClientName.isEmpty()) return false

        viewModelScope.launch {
            appointmentRepository.insert(
                Appointment(
                    clientName = normalizedClientName,
                    masterName = master.name,
                    serviceTitle = service.title,
                    price = service.price,
                    dateTime = dateTime,
                    status = "PENDING"
                )
            )
        }
        return true
    }

    fun updateStatus(appointment: Appointment, status: String) {
        if (appointment.status != "PENDING") return
        if (status != "DONE" && status != "CANCELLED") return

        viewModelScope.launch {
            appointmentRepository.updateStatus(appointment.id, status)
        }
    }

    fun delete(appointment: Appointment) {
        viewModelScope.launch {
            appointmentRepository.delete(appointment)
        }
    }

    class Factory(
        private val appointmentRepository: AppointmentRepository,
        private val masterRepository: MasterRepository,
        private val serviceRepository: ServiceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(AppointmentsViewModel::class.java))
            return AppointmentsViewModel(
                appointmentRepository,
                masterRepository,
                serviceRepository
            ) as T
        }
    }
}