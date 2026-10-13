package com.example.beautysalon.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.data.repository.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.entity.Service
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
            appointments = if (filter == null) appointments else appointments.filter {
                it.masterName == filter
            },
            masters = masters,
            services = services,
            selectedMaster = filter
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppointmentsUiState()
    )

    fun selectMaster(name: String?) {
        selectedMaster.value = name
    }

    fun createAppointment(
        clientName: String,
        service: Service,
        master: Master,
        time: String,
        onInvalidTime: () -> Unit
    ) {
        val dateTime = DateUtils.parseTimeToToday(time)
        if (dateTime == null) {
            onInvalidTime()
            return
        }

        viewModelScope.launch {
            appointmentRepository.insert(
                Appointment(
                    clientName = clientName.trim(),
                    masterName = master.name,
                    serviceTitle = service.title,
                    price = service.price,
                    dateTime = dateTime,
                    status = "PENDING"
                )
            )
        }
    }

    fun updateStatus(appointment: Appointment, status: String) {
        if (appointment.status != "PENDING") return
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