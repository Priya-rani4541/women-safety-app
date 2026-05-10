package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SOSSentViewModel : ViewModel() {
    private val _locationName = mutableStateOf("Connaught Place, New Delhi")
    val locationName: State<String> = _locationName

    private val _coordinates = mutableStateOf("28.6315° N, 77.2167° E")
    val coordinates: State<String> = _coordinates

    private val _notifiedGuardians = mutableStateOf("Meera, Rajesh & Anjali notified")
    val notifiedGuardians: State<String> = _notifiedGuardians
}
