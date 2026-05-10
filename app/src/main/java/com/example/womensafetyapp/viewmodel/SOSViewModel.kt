package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SOSViewModel : ViewModel() {
    private val _countdown = mutableIntStateOf(3)
    val countdown: State<Int> = _countdown

    private val _isSending = mutableStateOf(false)
    val isSending: State<Boolean> = _isSending

    fun startCountdown(onAlertSent: () -> Unit) {
        viewModelScope.launch {
            while (_countdown.intValue > 0) {
                delay(1000)
                _countdown.intValue--
            }
            _isSending.value = true
            onAlertSent()
        }
    }
}
