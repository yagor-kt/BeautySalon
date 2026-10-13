package com.example.beautysalon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "masters")
data class Master(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val specialization: String,
    val experienceYears: Int,
    val rating: Float,
    val photoUrl: String,
    val description: String,
    val dailySlots: Int
)