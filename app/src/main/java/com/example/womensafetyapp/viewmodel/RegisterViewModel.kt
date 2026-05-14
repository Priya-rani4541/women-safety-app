package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    // -----------------------------
    // FULL NAME
    // -----------------------------

    var fullName by mutableStateOf("")
        private set

    // -----------------------------
    // EMAIL
    // -----------------------------

    var email by mutableStateOf("")
        private set

    // -----------------------------
    // PHONE
    // -----------------------------

    var phone by mutableStateOf("")
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
    // UPDATE FULL NAME
    // -----------------------------

    fun onFullNameChange(
        newName: String
    ) {

        fullName = newName
    }

    // -----------------------------
    // UPDATE EMAIL
    // -----------------------------

    fun onEmailChange(
        newEmail: String
    ) {

        email = newEmail
    }

    // -----------------------------
    // UPDATE PHONE
    // -----------------------------

    fun onPhoneChange(
        newPhone: String
    ) {

        phone = newPhone
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
    // CLEAR ALL FIELDS
    // -----------------------------

    fun clearFields() {

        fullName = ""

        email = ""

        phone = ""

        password = ""
    }
}