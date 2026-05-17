package com.example.womensafetyapp.data.model


data class SosAlert(
    val userName: String = "",
    val phone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)