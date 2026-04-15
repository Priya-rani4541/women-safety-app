package com.example.womensafetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
class MainActivity : ComponentActivity() {
    var currentLat by mutableStateOf(0.0)
    var currentLng by mutableStateOf(0.0)
    var statusMessage by mutableStateOf("Fetching location...")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) getLocation()
        else statusMessage = "Location permission denied"
    }

    // SMS permission launcher
    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) sendSOSMessage()   // Permission granted → now send
        else statusMessage = "SMS permission denied. Cannot send SOS."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            WomenSafetyAppTheme {
                HomeScreen(
                    lat = currentLat,
                    lng = currentLng,
                    status = statusMessage,
                    onSOSClick = { sendSOS() }
                )
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLng = location.longitude
                statusMessage = "Location ready ✅"
            } else {
                statusMessage = "Could not get location ❌"
            }
        }
    }

    fun sendSOS() {
        // ✅ Check SMS permission FIRST before doing anything
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendSOSMessage() // Already have permission
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS) // Ask first
        }
    }

    private fun sendSOSMessage() {
        val emergencyContact = "+918360295190" // Replace with real number

        val message = "🆘 SOS! I need help! My location: " +
                "https://maps.google.com/?q=$currentLat,$currentLng"

        try {
            val smsManager = getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(emergencyContact, null, message, null, null)
            statusMessage = "SOS Sent ✅ to $emergencyContact"
            Log.d("SOS", "SMS sent successfully")
        } catch (e: Exception) {
            statusMessage = "SOS Failed ❌: ${e.message}"
            Log.e("SOS", "Failed: ${e.message}")
        }
    }
}
@Composable
fun HomeScreen(
    lat: Double,
    lng: Double,
    status: String,
    onSOSClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            Text(text = if (lat == 0.0) "Fetching location..." else "Lat: $lat\nLng: $lng")
            Text(text = status) // ← Shows SOS result or errors

            Button(onClick = onSOSClick) {
                Text("🆘 SEND SOS")
            }
        }
    }
}