package com.example.desiredvacationsapp.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vacation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val hotelName: String,
    val location : String,
    val necessaryMoneyAmount: Double,
    val description: String,
    val imageName: String?
)