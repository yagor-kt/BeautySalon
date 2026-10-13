package com.example.beautysalon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val masterName: String,
    val serviceTitle: String,
    val price: Int,
    val dateTime: Long,
    val status: String
)