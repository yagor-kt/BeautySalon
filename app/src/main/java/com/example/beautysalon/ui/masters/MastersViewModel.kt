package com.example.beautysalon.ui.masters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MasterWithLoad(
    val master: Master,
    val loaded: Int
)

data class MastersUiState(
    val masters: List<MasterWithLoad> = emptyList()
)

class MastersViewModel(
    masterRepository: MasterRepository,
    appointmentRepository: AppointmentRepository
) : ViewModel() {
    private val start = DateUtils.startOfDay()
    private val end = DateUtils.endOfDay()

    val state = combine(
        masterRepository.getAll(),
        appointmentRepository.getByDate(start, end)
    ) { masters, appointments ->
        MastersUiState(
            masters = masters.map { master ->
                MasterWithLoad(
                    master = master,
                    loaded = appointments.count { it.masterName == master.name }
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MastersUiState()
    )

    class Factory(
        private val masterRepository: MasterRepository,
        private val appointmentRepository: AppointmentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(MastersViewModel::class.java))
            return MastersViewModel(masterRepository, appointmentRepository) as T
        }
    }
}