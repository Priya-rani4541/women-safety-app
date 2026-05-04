package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.womensafetyapp.navigation.SetupNavGraph
import com.example.womensafetyapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    // ✅ Correct ViewModel initialization
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            SetupNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}