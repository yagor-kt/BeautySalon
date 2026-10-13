package com.example.beautysalon.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautysalon.R
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.ui.components.SalonCard
import com.example.beautysalon.ui.components.StatusChip
import com.example.beautysalon.util.DateUtils
import com.example.beautysalon.util.MoneyUtils

@Composable
fun HistoryScreen(repository: AppointmentRepository) {
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.Factory(repository)
    )
    val history by viewModel.history.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.nav_history),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.tab_history)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.tab_analytics)) }
            )
        }

        if (selectedTab == 0) {
            HistoryList(history)
        } else {
            AnalyticsContent(analytics)
        }
    }
}

@Composable
private fun HistoryList(appointments: List<Appointment>) {
    if (appointments.isEmpty()) {
        Text(
            text = stringResource(R.string.no_data),
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        var previousMonth: String? = null

        appointments.forEach { appointment ->
            val currentMonth = DateUtils.monthName(appointment.dateTime)
            if (currentMonth != previousMonth) {
                item(key = "month_$currentMonth") {
                    Text(
                        text = currentMonth,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                previousMonth = currentMonth
            }
            item(key = "history_${appointment.id}") {
                HistoryAppointmentCard(appointment)
            }
        }
    }
}

@Composable
private fun HistoryAppointmentCard(appointment: Appointment) {
    SalonCard {
        Text(
            text = DateUtils.formatDateTime(appointment.dateTime),
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = appointment.serviceTitle)
        Text(text = appointment.clientName)
        Text(text = appointment.masterName)
        StatusChip(status = appointment.status)
        Text(text = MoneyUtils.format(appointment.price))
    }
}

@Composable
private fun AnalyticsContent(state: AnalyticsUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AnalyticsNumberCard(
                title = stringResource(R.string.monthly_revenue),
                value = MoneyUtils.format(state.monthRevenue)
            )
        }
        item {
            AnalyticsNumberCard(
                title = stringResource(R.string.weekly_revenue),
                value = MoneyUtils.format(state.weekRevenue)
            )
        }
        item {
            SalonCard {
                Text(
                    text = stringResource(R.string.top_services),
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.topServices.isEmpty()) {
                    Text(text = stringResource(R.string.no_data))
                } else {
                    state.topServices.forEach { item ->
                        Text(
                            text = stringResource(
                                R.string.service_count,
                                item.title,
                                item.count
                            )
                        )
                    }
                }
            }
        }
        item {
            SalonCard {
                Text(
                    text = stringResource(R.string.top_masters),
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.topMasters.isEmpty()) {
                    Text(text = stringResource(R.string.no_data))
                } else {
                    state.topMasters.forEach { item ->
                        Text(
                            text = stringResource(
                                R.string.master_count,
                                item.name,
                                item.count
                            )
                        )
                    }
                }
            }
        }
        item {
            SalonCard {
                Text(
                    text = stringResource(R.string.cancellations_month),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        R.string.analytics_cancellation_count,
                        state.cancelledCount,
                        state.cancellationPercent
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun AnalyticsNumberCard(
    title: String,
    value: String
) {
    SalonCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = value, style = MaterialTheme.typography.headlineMedium)
    }
}