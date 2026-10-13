package com.example.beautysalon.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.MasterCount
import com.example.beautysalon.data.ServiceCount
import com.example.beautysalon.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val monthRevenue: Int = 0,
    val weekRevenue: Int = 0,
    val topServices: List<ServiceCount> = emptyList(),
    val topMasters: List<MasterCount> = emptyList(),
    val cancelledCount: Int = 0,
    val cancellationPercent: Int = 0
)

class HistoryViewModel(
    repository: AppointmentRepository
) : ViewModel() {
    private val now = System.currentTimeMillis()
    private val monthStart = DateUtils.monthStart()
    private val weekStart = DateUtils.weekAgo()

    val history = repository.getHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val monthRevenue = repository.revenueBetween(monthStart, now)
    private val weekRevenue = repository.revenueBetween(weekStart, now)
    private val topServices = repository.topServices(monthStart, now)
    private val topMasters = repository.topMasters(monthStart, now)
    private val cancelled = repository.cancelledBetween(monthStart, now)
    private val monthAppointments = repository.getByDate(monthStart, now)

    private val summary = combine(
        monthRevenue,
        weekRevenue,
        topServices,
        topMasters,
        cancelled
    ) { revenueMonth, revenueWeek, services, masters, cancelledCount ->
        SummaryPart(
            monthRevenue = revenueMonth ?: 0,
            weekRevenue = revenueWeek ?: 0,
            topServices = services,
            topMasters = masters,
            cancelledCount = cancelledCount
        )
    }

    val analytics = combine(summary, monthAppointments) { summaryPart, appointments ->
        val total = appointments.size
        val percentage = if (total > 0) {
            (summaryPart.cancelledCount * 100f / total).toInt()
        } else {
            0
        }
        AnalyticsUiState(
            monthRevenue = summaryPart.monthRevenue,
            weekRevenue = summaryPart.weekRevenue,
            topServices = summaryPart.topServices,
            topMasters = summaryPart.topMasters,
            cancelledCount = summaryPart.cancelledCount,
            cancellationPercent = percentage
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AnalyticsUiState()
    )

    private data class SummaryPart(
        val monthRevenue: Int,
        val weekRevenue: Int,
        val topServices: List<ServiceCount>,
        val topMasters: List<MasterCount>,
        val cancelledCount: Int
    )

    class Factory(
        private val repository: AppointmentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(HistoryViewModel::class.java))
            return HistoryViewModel(repository) as T
        }
    }
}