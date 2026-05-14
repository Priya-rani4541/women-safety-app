package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SOSSentViewModel : ViewModel() {

    // -----------------------------
    // LOCATION NAME
    // -----------------------------

    var locationName by mutableStateOf(
        "Connaught Place, New Delhi"
    )
        private set

    // -----------------------------
    // COORDINATES
    // -----------------------------

    var coordinates by mutableStateOf(
        "28.6315° N, 77.2167° E"
    )
        private set

    // -----------------------------
    // NOTIFIED GUARDIANS
    // -----------------------------

    var notifiedGuardians by mutableStateOf(
        "No guardians notified yet"
    )
        private set

    // -----------------------------
    // UPDATE LOCATION
    // -----------------------------

    fun updateLocation(
        newLocation: String
    ) {

        locationName = newLocation
    }

    // -----------------------------
    // UPDATE COORDINATES
    // -----------------------------

    fun updateCoordinates(
        newCoordinates: String
    ) {

        coordinates = newCoordinates
    }

    // -----------------------------
    // UPDATE NOTIFIED GUARDIANS
    // -----------------------------

    fun updateNotifiedGuardians(
        guardians: String
    ) {

        notifiedGuardians = guardians
    }

    // -----------------------------
    // RESET SOS STATE
    // -----------------------------

    fun resetSOSData() {

        locationName = ""

        coordinates = ""

        notifiedGuardians = ""
    }
}