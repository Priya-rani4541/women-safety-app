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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

// ─── Colors ────────────────────────────────────────────────────────────────────
private val SOSBg = Color(0xFF0A0010)
private val SOSRedMain = Color(0xFFD93025)
private val SOSRedGlow = Color(0xFFE8325A)
private val SOSCountdown = Color(0xFFD93025)
private val WhiteSOS = Color(0xFFFFFFFF)
private val TextGraySOS = Color(0xFF9B8BB0)
private val CancelBg = Color(0xFF2A1A3B)
private val AvatarPurple = Color(0xFF7C3AED)
private val AvatarPink = Color(0xFFD946A8)

@Composable
fun SOSScreen(
    onCancel: () -> Unit = {},
    onAlertSent: () -> Unit = {}
) {

    val context = LocalContext.current

    val firestore = FirebaseFirestore.getInstance()

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // ─── SAVE SOS ALERT FUNCTION ───────────────────────────────────────────────
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

    // ─── LOCATION PERMISSION ──────────────────────────────────────────────────
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->

                if (isGranted) {

                    SOSManager.sendSOS(context)

                    sendSOSAlert()

                } else {

                    Toast.makeText(
                        context,
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    var countdown by remember { mutableIntStateOf(3) }
    var isSending by remember { mutableStateOf(false) }

    // ─── COUNTDOWN LOGIC ──────────────────────────────────────────────────────
    LaunchedEffect(Unit) {

        while (countdown > 0) {
            delay(1000)
            countdown--
        }

        isSending = true

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            SOSManager.sendSOS(context)

            sendSOSAlert()

        } else {

            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // ─── ANIMATIONS ───────────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")

    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "r1"
    )

    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            tween(1100, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "r2"
    )

    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1300, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "r3"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    // ─── UI ───────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SOSBg),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val cx = size.width / 2f
            val cy = size.height / 2f
            val base = size.minDimension * 0.42f

            listOf(
                Pair(ring3Scale, 0.08f),
                Pair(ring2Scale, 0.13f),
                Pair(ring1Scale, 0.20f),
            ).forEach { (scale, alpha) ->

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SOSRedGlow.copy(alpha = alpha * glowAlpha / 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = base * scale
                    ),
                    radius = base * scale,
                    center = Offset(cx, cy)
                )

                drawCircle(
                    color = SOSRedGlow.copy(alpha = alpha * 0.5f),
                    radius = base * scale,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(0.12f))

            Text(
                "ALERT ACTIVATING IN",
                color = WhiteSOS.copy(alpha = 0.50f),
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            val countText =
                if (isSending) "SOS SENT" else "%02d".format(countdown)

            Text(
                countText,
                color = SOSCountdown,
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(0.05f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size((200 * ring3Scale).dp.coerceAtMost(200.dp))
                        .clip(CircleShape)
                        .background(SOSRedGlow.copy(alpha = 0.07f))
                )

                Box(
                    modifier = Modifier
                        .size((175 * ring2Scale).dp.coerceAtMost(175.dp))
                        .clip(CircleShape)
                        .background(SOSRedGlow.copy(alpha = 0.12f))
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
                            "SOS",
                            color = WhiteSOS,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            if (isSending) "SENDING" else "HOLD",
                            color = WhiteSOS.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text("📍", fontSize = 16.sp)

                Spacer(Modifier.width(6.dp))

                Text(
                    "Location being shared",
                    color = WhiteSOS,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Notifying your guardians",
                color = TextGraySOS,
                fontSize = 13.sp
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .padding(bottom = 52.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(CancelBg)
                    .clickable { onCancel() }
                    .padding(horizontal = 48.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "Cancel Alert",
                    color = WhiteSOS,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0010)
@Composable
fun SOSScreenPreview() {
    SOSScreen()
}