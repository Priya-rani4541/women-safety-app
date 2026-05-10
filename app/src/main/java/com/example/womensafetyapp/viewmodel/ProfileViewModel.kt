package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _notificationsEnabled = mutableStateOf(true)
    val notificationsEnabled: State<Boolean> = _notificationsEnabled

    private val _locationSharingEnabled = mutableStateOf(true)
    val locationSharingEnabled: State<Boolean> = _locationSharingEnabled

    private val _voiceTriggerEnabled = mutableStateOf(false)
    val voiceTriggerEnabled: State<Boolean> = _voiceTriggerEnabled

    private val _stealthModeEnabled = mutableStateOf(false)
    val stealthModeEnabled: State<Boolean> = _stealthModeEnabled

    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun toggleLocationSharing(enabled: Boolean) { _locationSharingEnabled.value = enabled }
    fun toggleVoiceTrigger(enabled: Boolean) { _voiceTriggerEnabled.value = enabled }
    fun toggleStealthMode(enabled: Boolean) { _stealthModeEnabled.value = enabled }
}
