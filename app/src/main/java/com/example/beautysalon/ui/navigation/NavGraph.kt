package com.example.beautysalon.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautysalon.R
import com.example.beautysalon.data.repository.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.ui.appointments.AppointmentsScreen
import com.example.beautysalon.ui.auth.LoginScreen
import com.example.beautysalon.ui.dashboard.DashboardScreen

@Composable
fun NavGraph(
    masterRepository: MasterRepository,
    serviceRepository: ServiceRepository,
    appointmentRepository: AppointmentRepository
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable(Screen.Login) {
            LoginScreen(navController)
        }
        composable(Screen.Dashboard) {
            DashboardScreen(
                navController = navController,
                masterRepository = masterRepository,
                appointmentRepository = appointmentRepository
            )
        }
        composable(Screen.Appointments) {
            AppointmentsScreen(
                navController = navController,
                appointmentRepository = appointmentRepository,
                masterRepository = masterRepository,
                serviceRepository = serviceRepository
            )
        }
        composable(Screen.Masters) {
            ComingSoonScreen()
        }
        composable(Screen.Services) {
            ComingSoonScreen()
        }
        composable(Screen.History) {
            ComingSoonScreen()
        }
    }
}

@Composable
private fun ComingSoonScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.screen_coming_soon),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}