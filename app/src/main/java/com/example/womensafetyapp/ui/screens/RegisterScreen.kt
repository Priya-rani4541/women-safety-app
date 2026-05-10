package com.example.womensafetyapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.ui.components.*
import com.example.womensafetyapp.viewmodel.AuthViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegisterScreen(
    onSignIn: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {

    // ✅ LOCAL UI STATE (Correct way)
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Observe state
    val state = viewModel.state
    val isLoading = viewModel.isLoading
    val isLoggedIn = viewModel.isLoggedIn

    // Success navigation
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onRegisterSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF5FF))
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(40.dp))

            Column(modifier = Modifier.padding(24.dp)) {

                Text("Create Account", fontSize = 28.sp)

                Spacer(Modifier.height(20.dp))

                // Name
                ShieldTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Full Name",
                    icon = Icons.Default.Person
                )

                Spacer(Modifier.height(12.dp))

                // Phone
                ShieldTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Phone",
                    icon = Icons.Default.Phone
                )

                Spacer(Modifier.height(12.dp))

                // Email
                ShieldTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    icon = Icons.Default.Email
                )

                Spacer(Modifier.height(12.dp))

                // Password
                ShieldTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    showPassword = showPass,
                    onTogglePass = { showPass = !showPass }
                )

                Spacer(Modifier.height(24.dp))

                // Register Button
                GradientButton(
                    text = "Create My Shield",
                    onClick = {
                        if (fullName.isNotBlank() &&
                            phone.isNotBlank() &&
                            email.isNotBlank() &&
                            password.isNotBlank()
                        ) {
                            viewModel.register(fullName, email, password)
                        } else {
                            Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                Text(text = state)

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("Already have account? ")
                    Text(
                        "Sign In",
                        modifier = Modifier.clickable { onSignIn() },
                        color = Color.Blue
                    )
                }
            }
        }

        // Loader
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}