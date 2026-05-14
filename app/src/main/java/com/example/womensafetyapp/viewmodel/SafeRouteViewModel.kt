package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SafeRouteViewModel : ViewModel() {

    // -----------------------------
    // ORIGIN
    // -----------------------------

    var origin by mutableStateOf(
        "Connaught Place, Delhi"
    )
        private set

    // -----------------------------
    // DESTINATION
    // -----------------------------

    var destination by mutableStateOf("")
        private set

    // -----------------------------
    // UPDATE DESTINATION
    // -----------------------------

    fun onDestinationChange(
        newDestination: String
    ) {

        destination = newDestination
    }

    // -----------------------------
    // UPDATE ORIGIN
    // -----------------------------

    fun onOriginChange(
        newOrigin: String
    ) {

        origin = newOrigin
    }

    // -----------------------------
    // SWAP LOCATIONS
    // -----------------------------

    fun swapLocations() {

        val temp = origin

        origin = destination

        destination = temp
    }

    // -----------------------------
    // CLEAR DESTINATION
    // -----------------------------

    fun clearDestination() {

        destination = ""
    }
}