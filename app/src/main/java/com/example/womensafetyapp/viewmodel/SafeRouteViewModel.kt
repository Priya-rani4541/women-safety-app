package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SafeRouteViewModel : ViewModel() {
    private val _origin = mutableStateOf("Connaught Place, Delhi")
    val origin: State<String> = _origin

    private val _destination = mutableStateOf("")
    val destination: State<String> = _destination

    fun onDestinationChange(newDest: String) {
        _destination.value = newDest
    }
}
