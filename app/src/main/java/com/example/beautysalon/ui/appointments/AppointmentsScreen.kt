package com.example.beautysalon.ui.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.beautysalon.R
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.data.repository.AppointmentRepository
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.entity.Service
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.ui.components.SalonCard
import com.example.beautysalon.ui.components.StatusChip
import com.example.beautysalon.util.DateUtils
import com.example.beautysalon.util.MoneyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    navController: NavHostController,
    appointmentRepository: AppointmentRepository,
    masterRepository: MasterRepository,
    serviceRepository: ServiceRepository
) {
    val viewModel: AppointmentsViewModel = viewModel(
        factory = AppointmentsViewModel.Factory(
            appointmentRepository,
            masterRepository,
            serviceRepository
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateSheet by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateSheet = true }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_description_add)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.nav_appointments),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedMaster == null,
                        onClick = { viewModel.selectMaster(null) },
                        label = { Text(stringResource(R.string.all_masters)) }
                    )
                }
                items(state.masters) { master ->
                    FilterChip(
                        selected = state.selectedMaster == master.name,
                        onClick = { viewModel.selectMaster(master.name) },
                        label = { Text(master.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (state.appointments.isEmpty()) {
                Text(text = stringResource(R.string.empty_appointments))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.appointments, key = { it.id }) { appointment ->
                        SalonCard(
                            modifier = Modifier.clickable {
                                selectedAppointment = appointment
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = DateUtils.formatTime(appointment.dateTime),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                StatusChip(status = appointment.status)
                            }
                            Text(text = appointment.serviceTitle)
                            Text(text = appointment.clientName)
                            Text(text = appointment.masterName)
                        }
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        val invalidTimeMessage = stringResource(R.string.invalid_time)
        ModalBottomSheet(onDismissRequest = { showCreateSheet = false }) {
            CreateAppointmentSheet(
                services = state.services,
                masters = state.masters,
                onCreate = { client, service, master, time ->
                    viewModel.createAppointment(client, service, master, time) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                invalidTimeMessage
                            )
                        }
                    }
                    if (DateUtils.parseTimeToToday(time) != null) {
                        showCreateSheet = false
                    }
                }
            )
        }
    }

    selectedAppointment?.let { appointment ->
        var confirmDelete by remember(appointment.id) { mutableStateOf(false) }
        ModalBottomSheet(onDismissRequest = { selectedAppointment = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.appointment_details),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = DateUtils.formatDateTime(appointment.dateTime))
                Text(text = appointment.serviceTitle)
                Text(text = appointment.clientName)
                Text(text = appointment.masterName)
                Text(text = MoneyUtils.format(appointment.price))
                StatusChip(status = appointment.status)

                if (appointment.status == "PENDING") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.updateStatus(appointment, "DONE")
                                selectedAppointment = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.mark_done))
                        }
                        Button(
                            onClick = {
                                viewModel.updateStatus(appointment, "CANCELLED")
                                selectedAppointment = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel_appointment))
                        }
                    }
                }

                TextButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete_appointment))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (confirmDelete) {
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text(stringResource(R.string.delete_appointment)) },
                text = { Text(stringResource(R.string.delete_appointment_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.delete(appointment)
                            confirmDelete = false
                            selectedAppointment = null
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDelete = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun CreateAppointmentSheet(
    services: List<Service>,
    masters: List<Master>,
    onCreate: (String, Service, Master, String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedMaster by remember { mutableStateOf<Master?>(null) }
    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var masterMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.add_appointment),
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text(stringResource(R.string.client_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Column {
            Button(
                onClick = { serviceMenuExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = services.isNotEmpty()
            ) {
                Text(selectedService?.title ?: stringResource(R.string.service))
            }
            DropdownMenu(
                expanded = serviceMenuExpanded,
                onDismissRequest = { serviceMenuExpanded = false }
            ) {
                services.forEach { service ->
                    DropdownMenuItem(
                        text = { Text(service.title) },
                        onClick = {
                            selectedService = service
                            serviceMenuExpanded = false
                        }
                    )
                }
            }
        }

        Column {
            Button(
                onClick = { masterMenuExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = masters.isNotEmpty()
            ) {
                Text(selectedMaster?.name ?: stringResource(R.string.master))
            }
            DropdownMenu(
                expanded = masterMenuExpanded,
                onDismissRequest = { masterMenuExpanded = false }
            ) {
                masters.forEach { master ->
                    DropdownMenuItem(
                        text = { Text(master.name) },
                        onClick = {
                            selectedMaster = master
                            masterMenuExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text(stringResource(R.string.time)) },
            placeholder = { Text(stringResource(R.string.time_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val service = selectedService
                val master = selectedMaster
                if (clientName.isNotBlank() && service != null && master != null) {
                    onCreate(clientName, service, master, time)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = clientName.isNotBlank() && selectedService != null && selectedMaster != null
        ) {
            Text(stringResource(R.string.create_appointment))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}