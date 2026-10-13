package com.example.beautysalon.data

import androidx.room.ColumnInfo

data class ServiceCount(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "count") val count: Int
)