package com.example.womensafetyapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.womensafetyapp.SOSManager
import com.example.womensafetyapp.data.model.SosAlert
import com.example.womensafetyapp.utils.AudioRecorder
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

private val SOSBg = Color(0xFF0A0010)
private val SOSRedMain = Color(0xFFD93025)
private val SOSRedGlow = Color(0xFFE8325A)
private val SOSCountdown = Color(0xFFD93025)
private val WhiteSOS = Color(0xFFFFFFFF)

@Composable
fun SOSScreen(
    onCancel: () -> Unit = {},
    onAlertSent: () -> Unit = {}
) {

    val context = LocalContext.current

    val audioRecorder = remember {
        AudioRecorder(context)
    }

    val firestore = FirebaseFirestore.getInstance()

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    var countdown by remember {
        mutableIntStateOf(3)
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    // SAVE SOS ALERT
    @SuppressLint("MissingPermission")
    fun sendSOSAlert() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val sosAlert = SosAlert(
                        userName = "Priya",
                        phone = "9876543210",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    firestore.collection("sos_alerts")
                        .add(sosAlert)
                        .addOnSuccessListener {

                            Toast.makeText(
                                context,
                                "SOS Alert Sent!",
                                Toast.LENGTH_SHORT
                            ).show()

                            onAlertSent()
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                context,
                                "Failed to send SOS",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {

                    Toast.makeText(
                        context,
                        "Location not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // MULTIPLE PERMISSIONS
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->

                val locationGranted =
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

                val audioGranted =
                    permissions[Manifest.permission.RECORD_AUDIO] == true

                if (locationGranted && audioGranted) {

                    try {

                        audioRecorder.startRecording()

                        SOSManager.sendSOS(context)

                        sendSOSAlert()

                    } catch (e: Exception) {

                        Toast.makeText(
                            context,
                            "Audio recording failed",
                            Toast.LENGTH_SHORT
                        ).show()

                        e.printStackTrace()
                    }

                } else {

                    Toast.makeText(
                        context,
                        "Permissions Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    // MAIN SOS FLOW
    LaunchedEffect(Unit) {

        while (countdown > 0) {

            delay(1000)

            countdown--
        }

        isSending = true

        val locationPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val audioPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        if (locationPermission && audioPermission) {

            try {

                audioRecorder.startRecording()

                SOSManager.sendSOS(context)

                sendSOSAlert()

                // WAIT 10 SEC
//                delay(10000)

                // STOP RECORDING
//                audioRecorder.stopRecording()

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    "Recorder error",
                    Toast.LENGTH_SHORT
                ).show()

                e.printStackTrace()
            }

        } else {

            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    // ANIMATIONS
    val infiniteTransition =
        rememberInfiniteTransition(label = "pulse")

    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(900),
            RepeatMode.Reverse
        ),
        label = "r1"
    )

    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            tween(1100),
            RepeatMode.Reverse
        ),
        label = "r2"
    )

    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1300),
            RepeatMode.Reverse
        ),
        label = "r3"
    )

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SOSBg),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val cx = size.width / 2f
            val cy = size.height / 2f
            val base = size.minDimension * 0.42f

            listOf(
                ring3Scale,
                ring2Scale,
                ring1Scale
            ).forEach { scale ->

                drawCircle(
                    color = SOSRedGlow.copy(alpha = 0.15f),
                    radius = base * scale,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(0.15f))

            Text(
                text = "ALERT ACTIVATING IN",
                color = WhiteSOS.copy(alpha = 0.7f),
                fontSize = 12.sp,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isSending) "SOS SENT" else countdown.toString(),
                color = SOSCountdown,
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(SOSRedMain),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "SOS",
                    color = WhiteSOS,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SOSScreenPreview() {
    SOSScreen()
}