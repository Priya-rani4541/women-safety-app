package com.example.womensafetyapp.ui.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.ui.components.GradientButton
import com.example.womensafetyapp.ui.components.ShieldTextField
import com.example.womensafetyapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val state = viewModel.state
    val isLoading = viewModel.isLoading
    val isLoggedIn = viewModel.isLoggedIn

    // ✅ Navigate on success
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // ✅ GOOGLE LOGIN HANDLER
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (!idToken.isNullOrEmpty()) {
                viewModel.googleLogin(idToken)
            } else {
                Toast.makeText(context, "Google token error", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF5FF))
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A0E6B), Color(0xFF3D1A8A))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        "Welcome Back",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Sign in to your account",
                        color = Color.White.copy(0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                InputLabel("EMAIL ADDRESS")

                ShieldTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "your@email.com",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputLabel("PASSWORD")

                ShieldTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    showPassword = showPass,
                    onTogglePass = { showPass = !showPass }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Forgot password?",
                    color = Color(0xFFE8325A),
                    modifier = Modifier.clickable { onForgotPassword() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // LOGIN BUTTON
                GradientButton(
                    text = "Sign In Securely",
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                            return@GradientButton
                        }

                        viewModel.login(email, password)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ERROR MESSAGE
                if (state.isNotEmpty()) {
                    Text(
                        text = state,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // GOOGLE SIGN IN
                OutlinedButton(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("YOUR_WEB_CLIENT_ID") // 🔥 replace this
                            .requestEmail()
                            .build()

                        val activity = context as? ComponentActivity
                        activity?.let {
                            val client = GoogleSignIn.getClient(it, gso)
                            googleLauncher.launch(client.signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Sign in with Google")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("New here? ")
                    Text(
                        "Create account",
                        color = Color(0xFFE8325A),
                        modifier = Modifier.clickable { onCreateAccount() }
                    )
                }
            }
        }

        // LOADING UI
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

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFF9B8BB0),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}