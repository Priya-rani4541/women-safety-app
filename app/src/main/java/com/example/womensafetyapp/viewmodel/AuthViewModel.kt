package com.example.womensafetyapp.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    // 🔹 UI STATE
    var state by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    // ✅ AUTO LOGIN (IMPORTANT)
    init {
        isLoggedIn = auth.currentUser != null
    }

    // 🔐 LOGIN (EMAIL)
    fun login(email: String, password: String) {
        Log.d("LOGIN_DEBUG", "Login called with: $email")

        viewModelScope.launch {
            isLoading = true

            try {
                Log.d("LOGIN_DEBUG", "Calling Firebase login")

                state = repo.login(email, password)

                Log.d("LOGIN_DEBUG", "Result: $state")

                if (state == "Login Success") {
                    isLoggedIn = true
                }

            } catch (e: Exception) {
                Log.e("LOGIN_DEBUG", "Error: ${e.message}")
                state = e.message ?: "Login failed"
            }

            isLoading = false
        }
    }

    // 📝 REGISTER
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            isLoading = true

            try {
                val result = repo.register(name, email, password)
                state = result
            } catch (e: Exception) {
                state = e.message ?: "Registration failed"
            }

            isLoading = false
        }
    }

    // 🔁 RESET PASSWORD
    fun resetPassword(email: String) {
        viewModelScope.launch {
            isLoading = true

            try {
                val result = repo.resetPassword(email)
                state = result
            } catch (e: Exception) {
                state = e.message ?: "Reset failed"
            }

            isLoading = false
        }
    }

    // 🔵 GOOGLE LOGIN
    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            isLoading = true

            try {
                if (idToken.isBlank()) {
                    state = "Google token error"
                    isLoading = false
                    return@launch
                }

                val success = repo.googleLogin(idToken)

                if (success) {
                    isLoggedIn = true
                    state = "Google Sign-In Success"
                } else {
                    state = "Google Sign-In Failed"
                }

            } catch (e: Exception) {
                state = e.message ?: "Google login failed"
            }

            isLoading = false
        }
    }

    // 🔄 RESET STATE (for navigation reuse)
    fun resetState() {
        state = ""
    }

    // 🚪 LOGOUT
    fun logout() {
        repo.logout()
        isLoggedIn = false
        state = "Logged out"
    }
}