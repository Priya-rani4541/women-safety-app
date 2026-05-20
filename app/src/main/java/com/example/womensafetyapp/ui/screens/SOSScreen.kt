package com.example.womensafetyapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.example.womensafetyapp.utils.LiveTrackingManager
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

private val SOSBg = Color(0xFF0A0010)
private val SOSRedMain = Color(0xFFD93025)
private val SOSRedGlow = Color(0xFFE8325A)
private val WhiteSOS = Color.White

@Composable
fun SOSScreen(
    onCancel: () -> Unit = {},
    onAlertSent: () -> Unit = {}
) {

    val context = LocalContext.current

    val firestore = FirebaseFirestore.getInstance()

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    val audioRecorder = remember {
        AudioRecorder(context)
    }

    var countdown by remember {
        mutableIntStateOf(3)
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    // GUARDIANS COUNT
    var guardiansCount by remember {
        mutableIntStateOf(0)
    }

    // FETCH GUARDIANS COUNT
    LaunchedEffect(Unit) {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid

        if (uid != null) {

            firestore.collection("emergency_contacts")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->

                    guardiansCount = result.size()
                }
                .addOnFailureListener {

                    guardiansCount = 0
                }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendSOSAlert() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val currentUser =
                        FirebaseAuth.getInstance()
                            .currentUser

                    val sosAlert = SosAlert(

                        userName =
                            currentUser?.displayName
                                ?: "User",

                        phone =
                            currentUser?.phoneNumber
                                ?: "No Phone",

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
                        "Location not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->

                val locationGranted =
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

                val audioGranted =
                    permissions[Manifest.permission.RECORD_AUDIO] == true

                val smsGranted =
                    permissions[Manifest.permission.SEND_SMS] == true

                if (
                    locationGranted &&
                    audioGranted &&
                    smsGranted
                ) {

                    audioRecorder.startRecording()

                    val userId =
                        FirebaseAuth.getInstance()
                            .currentUser?.uid ?: ""

                    SOSManager.sendSOS(
                        context = context,
                        userId = userId,
                        audioRecorder = audioRecorder
                    )

                    // START LIVE TRACKING
                    LiveTrackingManager.startTracking(context)

                    sendSOSAlert()

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

        val smsPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

        if (
            locationPermission &&
            audioPermission &&
            smsPermission
        ) {

            audioRecorder.startRecording()

            val userId =
                FirebaseAuth.getInstance()
                    .currentUser?.uid ?: ""

            SOSManager.sendSOS(
                context = context,
                userId = userId,
                audioRecorder = audioRecorder
            )

            // START LIVE TRACKING
            LiveTrackingManager.startTracking(context)

            sendSOSAlert()

        } else {

            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.SEND_SMS
                )
            )
        }

        // STOP AUDIO AFTER 10 SEC
        delay(10000)

        audioRecorder.stopRecording()
    }

    // PULSE ANIMATION
    val infiniteTransition =
        rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
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

            val cx = size.width / 2
            val cy = size.height / 2

            drawCircle(
                color = SOSRedGlow.copy(alpha = 0.2f),
                radius = 250f * pulseScale,
                center = Offset(cx, cy),
                style = Stroke(width = 4f)
            )

            drawCircle(
                color = SOSRedGlow.copy(alpha = 0.1f),
                radius = 350f * pulseScale,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(0.12f))

            Text(
                text = "ALERT ACTIVATING IN",
                color = WhiteSOS.copy(alpha = 0.50f),
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text =
                    if (isSending)
                        "SOS SENT"
                    else
                        "%02d".format(countdown),

                color = SOSRedMain,
                fontSize = 88.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(30.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size((200 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(
                            SOSRedGlow.copy(alpha = 0.08f)
                        )
                )

                Box(
                    modifier = Modifier
                        .size((170 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(
                            SOSRedGlow.copy(alpha = 0.12f)
                        )
                )

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(SOSRedMain),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "SOS",
                            color = WhiteSOS,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                if (isSending)
                                    "SENDING"
                                else
                                    "HOLD",

                            color = WhiteSOS.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "📍 Location being shared",
                color = WhiteSOS,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Notifying $guardiansCount guardians",
                color = WhiteSOS.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(26.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                listOf("👩", "👨", "👩").forEach {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                SOSRedGlow.copy(alpha = 0.22f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = it,
                            fontSize = 28.sp
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .padding(bottom = 52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A1A3B))
                    .padding(
                        horizontal = 40.dp,
                        vertical = 16.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Cancel Alert",
                    color = WhiteSOS,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun SOSScreenPreview() {

    SOSScreen()
}