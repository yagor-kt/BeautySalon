package com.example.beautysalon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.beautysalon.data.AppDatabase
import com.example.beautysalon.data.AppointmentRepository
import com.example.beautysalon.data.repository.MasterRepository
import com.example.beautysalon.data.repository.ServiceRepository
import com.example.beautysalon.ui.navigation.NavGraph
import com.example.beautysalon.ui.theme.BeautySalonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val masterRepository = MasterRepository(database.masterDao())
        val serviceRepository = ServiceRepository(database.serviceDao())
        val appointmentRepository = AppointmentRepository(database.appointmentDao())

        setContent {
            BeautySalonTheme {
                NavGraph(
                    masterRepository = masterRepository,
                    serviceRepository = serviceRepository,
                    appointmentRepository = appointmentRepository
                )
            }
        }
    }
}