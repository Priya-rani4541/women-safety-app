package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    // -----------------------------
    // NOTIFICATIONS
    // -----------------------------

    var notificationsEnabled by mutableStateOf(true)
        private set

    // -----------------------------
    // LOCATION SHARING
    // -----------------------------

    var locationSharingEnabled by mutableStateOf(true)
        private set

    // -----------------------------
    // VOICE TRIGGER
    // -----------------------------

    var voiceTriggerEnabled by mutableStateOf(false)
        private set

    // -----------------------------
    // STEALTH MODE
    // -----------------------------

    var stealthModeEnabled by mutableStateOf(false)
        private set

    // -----------------------------
    // TOGGLE NOTIFICATIONS
    // -----------------------------

    fun toggleNotifications(
        enabled: Boolean
    ) {

        notificationsEnabled = enabled
    }

    // -----------------------------
    // TOGGLE LOCATION SHARING
    // -----------------------------

    fun toggleLocationSharing(
        enabled: Boolean
    ) {

        locationSharingEnabled = enabled
    }

    // -----------------------------
    // TOGGLE VOICE TRIGGER
    // -----------------------------

    fun toggleVoiceTrigger(
        enabled: Boolean
    ) {

        voiceTriggerEnabled = enabled
    }

    // -----------------------------
    // TOGGLE STEALTH MODE
    // -----------------------------

    fun toggleStealthMode(
        enabled: Boolean
    ) {

        stealthModeEnabled = enabled
    }
}