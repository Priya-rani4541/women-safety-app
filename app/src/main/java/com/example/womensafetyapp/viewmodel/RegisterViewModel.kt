package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _fullName = mutableStateOf("")
    val fullName: State<String> = _fullName

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _showPassword = mutableStateOf(false)
    val showPassword: State<Boolean> = _showPassword

    fun onFullNameChange(newName: String) { _fullName.value = newName }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPhoneChange(newPhone: String) { _phone.value = newPhone }
    fun onPasswordChange(newPass: String) { _password.value = newPass }
    fun togglePasswordVisibility() { _showPassword.value = !_showPassword.value }
}
