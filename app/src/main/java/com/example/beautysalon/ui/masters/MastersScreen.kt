package com.example.beautysalon.ui.masters

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.beautysalon.R
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.ui.components.LoadBar
import com.example.beautysalon.ui.components.SalonCard
import com.example.beautysalon.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MastersScreen(
    masterRepository: MasterRepository,
    appointmentRepository: AppointmentRepository
) {
    val viewModel: MastersViewModel = viewModel(
        factory = MastersViewModel.Factory(masterRepository, appointmentRepository)
    )
    val state by viewModel.state.collectAsState()
    var selectedMaster by remember { mutableStateOf<Master?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.nav_masters),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (state.masters.isEmpty()) {
            Text(text = stringResource(R.string.empty_masters))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.masters, key = { it.master.id }) { item ->
                    SalonCard(
                        modifier = Modifier.clickable {
                            selectedMaster = item.master
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            AsyncImage(
                                model = item.master.photoUrl,
                                contentDescription = stringResource(R.string.master_photo),
                                modifier = Modifier
                                    .height(76.dp)
                                    .clip(CircleShape)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = item.master.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(text = item.master.specialization)
                                RatingStars(rating = item.master.rating)
                            }
                        }
                        LoadBar(loaded = item.loaded, total = item.master.dailySlots)
                    }
                }
            }
        }
    }

    selectedMaster?.let { master ->
        ModalBottomSheet(onDismissRequest = { selectedMaster = null }) {
            MasterDetailsSheet(
                master = master,
                appointmentRepository = appointmentRepository
            )
        }
    }
}

@Composable
private fun RatingStars(rating: Float) {
    val filledStars = rating.toInt().coerceIn(0, 5)
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            Text(
                text = if (index < filledStars) "★" else "☆",
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(text = " %.1f".format(rating))
    }
}

@Composable
private fun MasterDetailsSheet(
    master: Master,
    appointmentRepository: AppointmentRepository
) {
    val appointments by appointmentRepository
        .getUpcomingByMaster(master.name, System.currentTimeMillis())
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = master.photoUrl,
            contentDescription = stringResource(R.string.master_photo),
            modifier = Modifier
                .height(120.dp)
                .clip(CircleShape)
        )
        Text(text = master.name, style = MaterialTheme.typography.titleLarge)
        Text(text = master.specialization)
        Text(text = stringResource(R.string.experience_years, master.experienceYears))
        RatingStars(rating = master.rating)
        Text(text = master.description)
        Text(
            text = stringResource(R.string.upcoming_appointments),
            style = MaterialTheme.typography.titleMedium
        )
        if (appointments.isEmpty()) {
            Text(text = stringResource(R.string.no_upcoming_appointments))
        } else {
            appointments.forEach { appointment ->
                Text(
                    text = "${DateUtils.formatDateTime(appointment.dateTime)} · ${appointment.serviceTitle} · ${appointment.clientName}"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}