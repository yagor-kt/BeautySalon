package com.example.beautysalon.ui.services

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautysalon.R
import com.example.beautysalon.data.entity.Service
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.ui.components.SalonCard
import com.example.beautysalon.util.MoneyUtils

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ServicesScreen(repository: ServiceRepository) {
    val viewModel: ServicesViewModel = viewModel(
        factory = ServicesViewModel.Factory(repository)
    )
    val services by viewModel.services.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_description_add)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.nav_services),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            services.groupBy { it.category }.forEach { (category, groupedServices) ->
                item(key = "category_$category") {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                items(groupedServices, key = { it.id }) { service ->
                    SalonCard(
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = { serviceToDelete = service }
                        )
                    ) {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = stringResource(R.string.duration_minutes, service.durationMin))
                        Text(text = MoneyUtils.format(service.price))
                    }
                }
            }

            if (services.isEmpty()) {
                item {
                    Text(text = stringResource(R.string.empty_services))
                }
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            AddServiceSheet(
                onAdd = { title, category, duration, price ->
                    viewModel.add(title, category, duration, price)
                    showAddSheet = false
                }
            )
        }
    }

    serviceToDelete?.let { service ->
        AlertDialog(
            onDismissRequest = { serviceToDelete = null },
            title = { Text(stringResource(R.string.delete_service_title)) },
            text = { Text(stringResource(R.string.delete_service_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(service)
                        serviceToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { serviceToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun AddServiceSheet(
    onAdd: (String, String, Int, Int) -> Unit
) {
    val categories = listOf(
        stringResource(R.string.category_manicure),
        stringResource(R.string.category_hairdressing),
        stringResource(R.string.category_cosmetology),
        stringResource(R.string.category_massage)
    )
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var duration by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.add_service),
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.service_title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Column {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(category)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            category = item
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it.filter(Char::isDigit) },
            label = { Text(stringResource(R.string.duration)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it.filter(Char::isDigit) },
            label = { Text(stringResource(R.string.price)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Button(
            onClick = {
                val durationValue = duration.toIntOrNull()
                val priceValue = price.toIntOrNull()
                if (
                    title.isNotBlank() &&
                    durationValue != null &&
                    durationValue > 0 &&
                    priceValue != null
                ) {
                    onAdd(title, category, durationValue, priceValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() &&
                    (duration.toIntOrNull() ?: 0) > 0 &&
                    price.toIntOrNull() != null
        ) {
            Text(stringResource(R.string.add))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}