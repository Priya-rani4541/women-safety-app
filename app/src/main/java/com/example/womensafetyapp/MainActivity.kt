package com.example.womensafetyapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.womensafetyapp.navigation.SetupNavGraph
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme
import com.example.womensafetyapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Permission Request Launcher
        val locationPermissionRequest =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                // You can later handle granted/denied permissions here
            }

        // Request Permissions
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            )
        )

        setContent {

            WomenSafetyAppTheme {

                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel()

                SetupNavGraph(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}