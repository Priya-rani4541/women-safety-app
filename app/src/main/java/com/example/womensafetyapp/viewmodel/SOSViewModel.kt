package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SOSViewModel : ViewModel() {

    // -----------------------------
    // COUNTDOWN TIMER
    // -----------------------------

    var countdown by mutableIntStateOf(3)
        private set

    // -----------------------------
    // SENDING STATE
    // -----------------------------

    var isSending by mutableStateOf(false)
        private set

    // -----------------------------
    // START COUNTDOWN
    // -----------------------------

    fun startCountdown(
        onAlertSent: () -> Unit
    ) {

        resetCountdown()

        viewModelScope.launch {

            while (countdown > 0) {

                delay(1000)

                countdown--
            }

            isSending = true

            onAlertSent()
        }
    }

    // -----------------------------
    // RESET COUNTDOWN
    // -----------------------------

    fun resetCountdown() {

        countdown = 3

        isSending = false
    }

    // -----------------------------
    // CANCEL SOS
    // -----------------------------

    fun cancelSOS() {

        resetCountdown()
    }
}