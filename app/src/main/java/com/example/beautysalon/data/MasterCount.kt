package com.example.beautysalon.data

import androidx.room.ColumnInfo

data class MasterCount(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count") val count: Int
)