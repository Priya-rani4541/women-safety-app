package com.example.womensafetyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
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
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    var currentLat   by mutableStateOf(0.0)
    var currentLng   by mutableStateOf(0.0)
    var statusMessage    by mutableStateOf("Fetching location…")
    var sosActive    by mutableStateOf(false)
    var contacts     by mutableStateOf(listOf("Contact not set"))

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // Location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if (isGranted[Manifest.permission.ACCESS_FINE_LOCATION] == true) getLocation()
        else statusMessage = "Location permission denied"
    }

    // SMS permission launcher
    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) sendSOSMessage()   // Permission granted → now send
        else {
            statusMessage = "SMS permission denied. Cannot send SOS."
            Toast.makeText(this, "SMS permission required for SOS!", Toast.LENGTH_LONG).show()
        }
    }

//   Registers a permission launcher and used to request multiple permissions at runtime
    private val allPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        // will be handled per feature
    }

    // Lifecycle of code : lifecycle = the different states your app/component goes through from creation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // load saved contacts
        val prefs = getSharedPreferences("safety_pref",MODE_PRIVATE)
        val saved = prefs.getStringSet("contacts", emptySet()) ?: emptySet()
        if(saved.isNotEmpty()) contacts = saved.toList()
        setContent {
            WomenSafetyAppTheme {
                MainScreen(
                    lat          = currentLat,
                    lng          = currentLng,
                    status       = statusMessage,
                    sosActive    = sosActive,
                    contacts     = contacts,
                    onSOSClick   = { triggerSOS() },
                    onSOSRelease = { cancelSOS() },
                    onAddContact = { number -> addContact(number) },
                    onRemoveContact = { number -> removeContact(number) },
                    onStartFakeCall  = { startFakeCall() },
                    onShareLocation  = { shareLocation() }
                )
            }
        }
        requestAllPermissions()
        startBackgroundService()
    }

    // Permissions
    private fun requestAllPermissions(){
        val needed = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.VIBRATE
        )
        allPermissions.launch((needed.toTypedArray()))

        // location
        if(ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) getLocation()
        else locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Location
    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLng = location.longitude
                statusMessage = "Location ready ✅"
                Log.d("LOCATION", "Lat : $currentLat, Lng : $currentLat")
            } else {
                statusMessage = "Location unavailable — check GPS"
            }
        }.addOnFailureListener { e ->
            statusMessage = "Location error : ${e.message}"
            Log.e("LOCATION", e.message ?: "uknown")
        }
    }

    //    SOS
    fun triggerSOS(){
        sosActive = true
        statusMessage = "SOS ACTIVE - Sending Alerts..."
        getLocation() // this willrefresh location first
        checkSmsAndSend()
    }
    private fun cancelSOS(){
        sosActive = false
        statusMessage = "SOS cancelled"
    }

    private fun checkSmsAndSend(){
        if(ActivityCompat.checkSelfPermission(
            this, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) sendSOSMessage()
        else smsPermissionLauncher.launch((Manifest.permission.SEND_SMS))
    }
    private fun sendSOSMessage() {
        if (contacts.isEmpty() || contacts.first() == "Contact not set") {
            statusMessage = "No emergency contacts set!"
            Toast.makeText(this, "Please add emergency contacts first", Toast.LENGTH_LONG).show()
            return
        }

        val locationText = if (currentLat != 0.0 && currentLng != 0.0)
            "https://maps.google.com/?q=$currentLat,$currentLng"
        else "Location unavailable — enable GPS"

        val message = "🆘 SOS ALERT! I need immediate help!\n" +
                "My location: $locationText\n" +
                "This is an automated emergency alert."

        val smsManager = getSystemService(SmsManager::class.java)

        var sentCount = 0
        contacts.forEach { contact ->
            if (contact == "Contact not set") return@forEach
            try {
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(contact, null, parts, null, null)
                sentCount++
                Log.d("SOS", "SMS sent to $contact")
            } catch (e: Exception) {
                Log.e("SOS", "Failed to send to $contact: ${e.message}")
            }
        }

        if (sentCount > 0) {
            statusMessage = "SOS sent to $sentCount contact(s) ✓"
            Toast.makeText(this, "SOS Sent to $sentCount contacts!", Toast.LENGTH_LONG).show()
        } else {
            statusMessage = "SOS FAILED — check contacts & SMS permission"
            Toast.makeText(this, "SOS send failed!", Toast.LENGTH_LONG).show()
        }
    }

    // Emergency Contacts
    private fun addContact(number: String) {
        val cleaned = number.trim()
        if (cleaned.isEmpty()) return

        val current = contacts.toMutableList()
        current.removeAll { it == "Contact not set" }
        if (!current.contains(cleaned)) current.add(cleaned)
        contacts = current

        val prefs = getSharedPreferences("safety_prefs", MODE_PRIVATE)
        prefs.edit().putStringSet("contacts", current.toSet()).apply()

        statusMessage = "Contact added: $cleaned"
        Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show()
    }
    private fun removeContact(number: String) {
        val current = contacts.toMutableList()
        current.remove(number)
        if (current.isEmpty()) current.add("Contact not set")
        contacts = current

        val prefs = getSharedPreferences("safety_prefs", MODE_PRIVATE)
        prefs.edit().putStringSet("contacts", current.filter { it != "Contact not set" }.toSet()).apply()
    }

    // for fake calls

    private fun startFakeCall() {
        val intent = Intent(this, FakeCallActivity::class.java)
        startActivity(intent)
    }

    // share location
    private fun shareLocation() {
        if (currentLat == 0.0) {
            Toast.makeText(this, "Location not available yet", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = "https://maps.google.com/?q=$currentLat,$currentLng"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "My live location: $uri")
        }
        startActivity(Intent.createChooser(intent, "Share location via"))
    }

    // background services
    private fun startBackgroundService() {
        val intent = Intent(this, BackgroundSafetyService::class.java)
        startForegroundService(intent)
    }

}
