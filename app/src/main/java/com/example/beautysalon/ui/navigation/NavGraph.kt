package com.example.beautysalon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.ui.appointments.AppointmentsScreen
import com.example.beautysalon.ui.auth.LoginScreen
import com.example.beautysalon.ui.dashboard.DashboardScreen
import com.example.beautysalon.ui.history.HistoryScreen
import com.example.beautysalon.ui.masters.MastersScreen
import com.example.beautysalon.ui.services.ServicesScreen

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
            MastersScreen(
                masterRepository = masterRepository,
                appointmentRepository = appointmentRepository
            )
        }
        composable(Screen.Services) {
            ServicesScreen(repository = serviceRepository)
        }
        composable(Screen.History) {
            HistoryScreen(repository = appointmentRepository)
        }
    }
}