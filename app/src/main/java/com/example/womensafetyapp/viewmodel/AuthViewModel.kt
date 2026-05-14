package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val auth = FirebaseAuth.getInstance()

    // --------------------------------------------------
    // UI STATES
    // --------------------------------------------------

    var isLoading by mutableStateOf(false)
        private set

    var isLoggedIn by mutableStateOf(
        auth.currentUser != null
    )
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    fun login(
        email: String,
        password: String
    ) {

        if (
            email.isBlank() ||
            password.isBlank()
        ) {

            errorMessage = "Please fill all fields"
            return
        }

        viewModelScope.launch {

            isLoading = true

            errorMessage = null
            successMessage = null

            try {

                val result =
                    repository.login(
                        email,
                        password
                    )

                result.onSuccess {

                    successMessage = it

                    isLoggedIn = true
                }

                result.onFailure {

                    errorMessage =
                        it.message ?: "Login failed"
                }

            } catch (e: Exception) {

                errorMessage =
                    e.message ?: "Login failed"
            }

            isLoading = false
        }
    }

    // --------------------------------------------------
    // REGISTER
    // --------------------------------------------------

    fun register(
        name: String,
        email: String,
        password: String
    ) {

        if (
            name.isBlank() ||
            email.isBlank() ||
            password.isBlank()
        ) {

            errorMessage = "Please fill all fields"
            return
        }

        viewModelScope.launch {

            isLoading = true

            errorMessage = null
            successMessage = null

            try {

                val result =
                    repository.register(
                        name,
                        email,
                        password
                    )

                result.onSuccess {

                    successMessage = it
                }

                result.onFailure {

                    errorMessage =
                        it.message ?: "Registration failed"
                }

            } catch (e: Exception) {

                errorMessage =
                    e.message ?: "Registration failed"
            }

            isLoading = false
        }
    }

    // --------------------------------------------------
    // GOOGLE LOGIN
    // --------------------------------------------------

    fun googleLogin(
        idToken: String
    ) {

        if (idToken.isBlank()) {

            errorMessage = "Google token missing"
            return
        }

        viewModelScope.launch {

            isLoading = true

            errorMessage = null
            successMessage = null

            try {

                val result =
                    repository.googleLogin(idToken)

                result.onSuccess {

                    successMessage = it

                    isLoggedIn = true
                }

                result.onFailure {

                    errorMessage =
                        it.message ?: "Google Sign-In Failed"
                }

            } catch (e: Exception) {

                errorMessage =
                    e.message ?: "Google login failed"
            }

            isLoading = false
        }
    }

    // --------------------------------------------------
    // RESET PASSWORD
    // --------------------------------------------------

    fun resetPassword(
        email: String
    ) {

        if (email.isBlank()) {

            errorMessage = "Enter email"
            return
        }

        viewModelScope.launch {

            isLoading = true

            errorMessage = null
            successMessage = null

            try {

                val result =
                    repository.resetPassword(email)

                result.onSuccess {

                    successMessage = it
                }

                result.onFailure {

                    errorMessage =
                        it.message ?: "Reset failed"
                }

            } catch (e: Exception) {

                errorMessage =
                    e.message ?: "Reset failed"
            }

            isLoading = false
        }
    }

    // --------------------------------------------------
    // LOGOUT
    // --------------------------------------------------

    fun logout() {

        repository.logout()

        isLoggedIn = false

        successMessage = "Logged out"
    }

    // --------------------------------------------------
    // CLEAR STATES
    // --------------------------------------------------

    fun clearMessages() {

        errorMessage = null

        successMessage = null
    }
}