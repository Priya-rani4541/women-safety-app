package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    // -----------------------------
    // EMAIL
    // -----------------------------

    var email by mutableStateOf("")
        private set

    // -----------------------------
    // PASSWORD
    // -----------------------------

    var password by mutableStateOf("")
        private set

    // -----------------------------
    // PASSWORD VISIBILITY
    // -----------------------------

    var showPassword by mutableStateOf(false)
        private set

    // -----------------------------
    // UPDATE EMAIL
    // -----------------------------

    fun onEmailChange(
        newEmail: String
    ) {

        email = newEmail
    }

    // -----------------------------
    // UPDATE PASSWORD
    // -----------------------------

    fun onPasswordChange(
        newPassword: String
    ) {

        password = newPassword
    }

    // -----------------------------
    // TOGGLE PASSWORD
    // -----------------------------

    fun togglePasswordVisibility() {

        showPassword = !showPassword
    }

    // -----------------------------
    // CLEAR FIELDS
    // -----------------------------

    fun clearFields() {

        email = ""

        password = ""
    }
}