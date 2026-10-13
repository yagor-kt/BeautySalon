package com.example.beautysalon.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.beautysalon.R
import com.example.beautysalon.data.repository.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.ui.components.LoadBar
import com.example.beautysalon.ui.components.SalonCard
import com.example.beautysalon.ui.navigation.Screen
import com.example.beautysalon.util.DateUtils

private data class DashboardDestination(
    val title: Int,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DashboardScreen(
    navController: NavHostController,
    masterRepository: MasterRepository,
    appointmentRepository: AppointmentRepository
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(
            masterRepository,
            appointmentRepository
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val destinations = listOf(
        DashboardDestination(
            R.string.nav_appointments,
            Icons.Default.CalendarMonth,
            Screen.Appointments
        ),
        DashboardDestination(
            R.string.nav_masters,
            Icons.Default.Spa,
            Screen.Masters
        ),
        DashboardDestination(
            R.string.nav_services,
            Icons.Default.ContentCut,
            Screen.Services
        ),
        DashboardDestination(
            R.string.nav_history,
            Icons.Default.History,
            Screen.History
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.salon_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.good_day),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SalonCard {
            Text(
                text = stringResource(R.string.today),
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = stringResource(R.string.appointments_count, state.appointmentsCount))
            Text(text = stringResource(R.string.completed_count, state.completedCount))
            Text(text = stringResource(R.string.cancelled_count, state.cancelledCount))
        }

        SalonCard {
            Text(
                text = stringResource(R.string.nearest_appointment),
                style = MaterialTheme.typography.titleMedium
            )

            val appointment = state.nearestAppointment
            if (appointment == null) {
                Text(text = stringResource(R.string.no_appointments_today))
            } else {
                Text(text = DateUtils.formatTime(appointment.dateTime))
                Text(text = appointment.serviceTitle)
                Text(text = appointment.clientName)
                Text(text = appointment.masterName)
            }
        }

        SalonCard {
            Text(
                text = stringResource(R.string.master_load),
                style = MaterialTheme.typography.titleMedium
            )

            state.masterLoads.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.master.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    LoadBar(
                        loaded = item.loaded,
                        total = item.master.dailySlots
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardDestinationCard(
                destination = destinations[0],
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(destinations[0].route) }
            )
            DashboardDestinationCard(
                destination = destinations[1],
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(destinations[1].route) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardDestinationCard(
                destination = destinations[2],
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(destinations[2].route) }
            )
            DashboardDestinationCard(
                destination = destinations[3],
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(destinations[3].route) }
            )
        }
    }
}

@Composable
private fun DashboardDestinationCard(
    destination: DashboardDestination,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = stringResource(destination.title))
        }
    }
}