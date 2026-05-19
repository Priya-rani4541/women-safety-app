package com.example.womensafetyapp.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Home
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Person
import android.Manifest
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.womensafetyapp.utils.LocationUtils
import com.example.womensafetyapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
import com.example.womensafetyapp.utils.AudioRecorder
import com.example.womensafetyapp.utils.ShakeDetector

// ─── Colors ────────────────────────────────────────────────────────────────────
private val HomeBg         = Color(0xFF0F0820)
private val HomeHeaderTop  = Color(0xFF1E0A3B)
private val HomeHeaderBot  = Color(0xFF0F0820)
private val CardDark       = Color(0xFF1A0F2E)
private val CardDarker     = Color(0xFF140B25)
private val CardLight      = Color(0xFFF5F0FC)
private val SOSRed         = Color(0xFFE8325A)
private val SOSRedDeep     = Color(0xFFC41F40)
private val SOSRing1       = Color(0xFFE8325A)
private val PinkAccent     = Color(0xFFE8325A)
private val PurpleAccent   = Color(0xFF9333EA)
private val WhiteHome      = Color(0xFFFFFFFF)
private val TextGrayHome   = Color(0xFF9B8BB0)
private val NavActive      = Color(0xFFE8325A)
private val NavInactive    = Color(0xFF5A4A6A)
private val GreenDot       = Color(0xFF22C55E)
private val GoldAccent     = Color(0xFFF4B942)

data class QuickItem(
    val icon: ImageVector,
    val label: String
)

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val screen: Screen
)

// Quick item accent colors
private val quickColors = listOf(
    Color(0xFFFFE4E8) to Color(0xFFE8325A),
    Color(0xFFE8F5E9) to Color(0xFF22C55E),
    Color(0xFFEDE9FE) to Color(0xFF9333EA),
    Color(0xFFFFF8E1) to Color(0xFFF4B942),
    Color(0xFFE3F2FD) to Color(0xFF2196F3),
    Color(0xFFFCE4EC) to Color(0xFFE91E63),
)



@Composable
fun HomeScreen(
    onSOSTriggered: () -> Unit = {},
    onNavigate: (Screen) -> Unit = {}
) {
    val context     = LocalContext.current
    val activity    = context as? Activity
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

    val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    var userName       by remember { mutableStateOf("User") }
    var guardianCount  by remember { mutableStateOf(0) }
    var selectedNav    by remember { mutableStateOf("Home") }
    var isFlashOn      by remember { mutableStateOf(false) }
    var sosPressed     by remember { mutableStateOf(false) }

    val quickItems = listOf(
        QuickItem(Icons.Default.Map,             "Safe Route"),
        QuickItem(Icons.Default.LocationOn,      "Live Track"),
        QuickItem(Icons.Default.People,          "Network"),
        QuickItem(Icons.Default.FlashlightOn,    "Flashlight"),
        QuickItem(Icons.Default.Call,            "Helpline"),
        QuickItem(Icons.Default.Mic,             "Record"),
    )

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val audioRecorder = remember {

        AudioRecorder(context)
    }

    var isTracking by remember {
        mutableStateOf(false)
    }

    // SHAKE logic
    DisposableEffect(Unit) {

        val shakeDetector = ShakeDetector {

            // WHAT HAPPENS ON SHAKE

            onSOSTriggered()

            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE)
                        as android.os.Vibrator

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                vibrator.vibrate(
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 400, 200, 400),
                        -1
                    )
                )

            } else {

                vibrator.vibrate(1000)
            }
        }

        sensorManager.registerListener(
            shakeDetector,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        onDispose {

            sensorManager.unregisterListener(shakeDetector)
        }
    }

    // ── Fetch user data ────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc -> userName = doc.getString("name") ?: "User" }
        db.collection("users").document(uid).collection("contacts").get()
            .addOnSuccessListener { result -> guardianCount = result.size() }
    }

    // ── Animations ─────────────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "sos")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue  = 0.88f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p1"
    )
    val pulse2 by infiniteTransition.animateFloat(
        initialValue  = 0.78f, targetValue = 1.22f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p2"
    )
    val pulse3 by infiniteTransition.animateFloat(
        initialValue  = 0.70f, targetValue = 1.30f,
        animationSpec = infiniteRepeatable(tween(1700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p3"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "glow"
    )
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue  = 1f, targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "blink"
    )
    val sosScale by animateFloatAsState(
        targetValue   = if (sosPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "sosScale"
    )

    // live time
    var currentTime by remember {
        mutableStateOf(
            SimpleDateFormat("EEEE, hh:mm a", Locale.getDefault()).format(Date()).uppercase()
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("EEEE, hh:mm a", Locale.getDefault())
                .format(Date()).uppercase()
            delay(60_000L) // update every minute
        }
    }

    LaunchedEffect(isTracking) {

        while (isTracking) {

            LocationUtils.getCurrentLocation(context) { lat, lng ->

                val uid =
                    FirebaseAuth.getInstance().currentUser?.uid
                        ?: return@getCurrentLocation

                FirebaseFirestore.getInstance()
                    .collection("live_locations")
                    .document(uid)
                    .set(
                        hashMapOf(
                            "latitude" to lat,
                            "longitude" to lng,
                            "timestamp" to System.currentTimeMillis()
                        )
                    )
            }

            delay(10000)
        }
    }

    // ── Scaffold ───────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = HomeBg,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF160C28), Color(0xFF0F0820)))
                    )
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier       = Modifier.background(Color.Transparent)
                ) {
                    listOf(
                        BottomNavItem(Icons.Default.Home,   "Home",    Screen.Home),
                        BottomNavItem(Icons.Default.Map,    "Route",   Screen.SafeRoute),
                        BottomNavItem(Icons.Default.People, "Network", Screen.CrowdNetwork),
                        BottomNavItem(Icons.Default.Person, "Profile", Screen.Profile),
                    ).forEach { (icon, label, screen) ->          // ✅ renamed to 'icon'
                        NavigationBarItem(
                            selected = selectedNav == label,
                            onClick  = { selectedNav = label; onNavigate(screen) },
                            icon     = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector        = icon,   // ✅ Icon composable, not Text
                                        contentDescription = label,
                                        tint               = if (selectedNav == label) NavActive else NavInactive,
                                        modifier           = Modifier.size(22.dp)
                                    )
                                    if (selectedNav == label) {
                                        Spacer(Modifier.height(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(NavActive)
                                        )
                                    }
                                }
                            },
                            label = {
                                Text(
                                    label,
                                    fontSize   = 10.sp,
                                    color      = if (selectedNav == label) NavActive else NavInactive,
                                    fontWeight = if (selectedNav == label) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor      = Color.Transparent,
                                selectedTextColor   = NavActive,
                                unselectedTextColor = NavInactive
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF2A0E6B), HomeHeaderBot))
                    )
                    .padding(start = 22.dp, end = 22.dp, top = 48.dp, bottom = 28.dp)
            ) {
                // Decorative top-right glow circle
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 50.dp, y = (-50).dp)
                        .clip(CircleShape)
                        .background(PinkAccent.copy(alpha = 0.08f))
                )

                Column {
                    // Top row: time + notification bell
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            currentTime,
                            color         = WhiteHome.copy(alpha = 0.40f),
                            fontSize      = 10.sp,
                            letterSpacing = 1.5.sp,
                            fontFamily    = FontFamily.Monospace
                        )
                        // Profile Avatar - top right
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PurpleAccent, PinkAccent)
                                    )
                                )
                                .clickable { onNavigate(Screen.Profile) },
                            contentAlignment = Alignment.Center
                        ) {
                            val photoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl
                            if (photoUrl != null) {
                                AsyncImage(
                                    model              = photoUrl,
                                    contentDescription = "Profile",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                // Fallback: first letter of name
                                Text(
                                    text       = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                    color      = WhiteHome,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Stay safe,",
                        color      = WhiteHome.copy(alpha = 0.65f),
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        "$userName 🛡️",
                        color      = WhiteHome,
                        fontSize   = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "$guardianCount guardian${if (guardianCount == 1) "" else "s"} watching over you",
                        color    = WhiteHome.copy(alpha = 0.60f),
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Status pills row
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Shield Active pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(WhiteHome.copy(alpha = 0.10f))
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(GreenDot.copy(alpha = blinkAlpha))
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                "Shield Active",
                                color      = WhiteHome.copy(alpha = 0.85f),
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        // Home Zone pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenDot.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍", fontSize = 11.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Home Zone",
                                color      = GreenDot,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── SOS Section ────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Card wrapper
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.radialGradient(
                                colors  = listOf(Color(0xFF2A0E2E), CardDark),
                                radius  = 600f
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(PinkAccent.copy(0.3f), PurpleAccent.copy(0.15f))),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background glow canvas
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(280.dp)) {
                        drawCircle(
                            brush  = Brush.radialGradient(
                                colors = listOf(SOSRed.copy(alpha = glowAlpha * 0.3f), Color.Transparent),
                                center = center,
                                radius = size.minDimension * 0.6f
                            ),
                            radius = size.minDimension * 0.6f,
                            center = center
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // SOS rings + button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier         = Modifier.size(screenWidth * 0.68f)
                        ) {
                            // Ring 3 (outermost)
                            Box(
                                modifier = Modifier
                                    .size((170 * pulse3).dp)
                                    .clip(CircleShape)
                                    .background(SOSRing1.copy(alpha = 0.06f))
                            )
                            // Ring 2
                            Box(
                                modifier = Modifier
                                    .size((140 * pulse2).dp)
                                    .clip(CircleShape)
                                    .background(SOSRing1.copy(alpha = 0.12f))
                            )
                            // Ring 1 (inner)
                            Box(
                                modifier = Modifier
                                    .size((115 * pulse1).dp)
                                    .clip(CircleShape)
                                    .background(SOSRing1.copy(alpha = 0.20f))
                            )

                            // SOS Button
                            Box(
                                modifier = Modifier
                                    .size(screenWidth * 0.33f)
                                    .scale(sosScale)
                                    .shadow(
                                        elevation    = 24.dp,
                                        shape        = CircleShape,
                                        ambientColor = SOSRed,
                                        spotColor    = SOSRed
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors  = listOf(SOSRed, SOSRedDeep),
                                            center  = Offset(0.35f, 0.35f),
                                            radius  = 300f
                                        )
                                    )
                                    .border(2.dp, WhiteHome.copy(alpha = 0.15f), CircleShape)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {

                                                sosPressed = true

                                                val startTime = System.currentTimeMillis()

                                                tryAwaitRelease()

                                                val endTime = System.currentTimeMillis()

                                                val holdDuration = endTime - startTime

                                                if (holdDuration >= 3000) {

                                                    onSOSTriggered()
                                                }

                                                sosPressed = false
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "SOS",
                                        color      = WhiteHome,
                                        fontSize   = 30.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        "HOLD 3s",
                                        color         = WhiteHome.copy(alpha = 0.80f),
                                        fontSize      = 10.sp,
                                        letterSpacing = 1.5.sp,
                                        fontFamily    = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Long press to activate emergency alert",
                            color     = TextGrayHome,
                            fontSize  = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        // Quick action row under SOS
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SOSQuickAction(
                                emoji = "📞",
                                label = "Call\nPolice"
                            ) {

                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:112")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }

                                context.startActivity(intent)
                            }

                            SOSQuickAction(
                                emoji = "📍",
                                label = "Share\nLocation"
                            ) {

                                val shareIntent = Intent().apply {

                                    action = Intent.ACTION_SEND

                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Emergency! My location:\nhttps://maps.google.com/?q=31.2240,75.7708"
                                    )

                                    type = "text/plain"
                                    `package` = "com.whatsapp"
                                }

                                try {

                                    context.startActivity(shareIntent)

                                } catch (e: Exception) {

                                    Toast.makeText(
                                        context,
                                        "WhatsApp not installed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            SOSQuickAction(
                                emoji = "📳",
                                label = "Alert\nVibrate"
                            ) {

                                val vibrator =
                                    context.getSystemService(Context.VIBRATOR_SERVICE)
                                            as Vibrator

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    vibrator.vibrate(
                                        VibrationEffect.createWaveform(
                                            longArrayOf(0, 500, 300, 500),
                                            -1
                                        )
                                    )

                                } else {

                                    vibrator.vibrate(2000)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Location Card ──────────────────────────────────────────────────────────
            var locationText by remember { mutableStateOf("Fetching location...") }

            LaunchedEffect(Unit) {
                try {
                    val geocoder = android.location.Geocoder(context, Locale.getDefault())
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE)
                            as android.location.LocationManager
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val location = locationManager.getLastKnownLocation(
                            android.location.LocationManager.GPS_PROVIDER
                        ) ?: locationManager.getLastKnownLocation(
                            android.location.LocationManager.NETWORK_PROVIDER
                        )
                        location?.let {
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addr = addresses[0]
                                locationText = buildString {
                                    addr.subLocality?.let  { append("$it, ") }
                                    addr.locality?.let     { append("$it, ") }
                                    addr.adminArea?.let    { append(it) }
                                }.trimEnd(',', ' ')
                            }
                        }
                    } else {
                        locationText = "Location permission needed"
                    }
                } catch (e: Exception) {
                    locationText = "Unable to fetch location"
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF1A0A3E), Color(0xFF0F1A3E)))
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(PurpleAccent.copy(0.3f), Color(0xFF2196F3).copy(0.3f))),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    // Left: location info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pulsing location dot
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint               = Color(0xFF2196F3),
                                modifier           = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Current Location",
                                color         = WhiteHome.copy(alpha = 0.45f),
                                fontSize      = 10.sp,
                                letterSpacing = 1.sp,
                                fontFamily    = FontFamily.Monospace
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                locationText,
                                color      = WhiteHome,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Right: Safe status badge
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenDot.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GreenDot.copy(alpha = blinkAlpha))
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Safe",
                                color      = GreenDot,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Route Active",
                            color    = WhiteHome.copy(alpha = 0.40f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Quick Access ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "QUICK ACCESS",
                        color         = WhiteHome.copy(alpha = 0.50f),
                        fontSize      = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight    = FontWeight.Bold,
                        fontFamily    = FontFamily.Monospace
                    )
                    Text(
                        "See all →",
                        color    = PinkAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(12.dp))

                quickItems.chunked(3).forEach { rowItems ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEachIndexed { idx, item ->
                            val colorIndex = quickItems.indexOf(item)
                            val (bgColor, accentColor) = quickColors.getOrElse(colorIndex) {
                                Color(0xFFEDE9FE) to PurpleAccent
                            }
                            QuickAccessCard(
                                item        = item,
                                bgColor     = bgColor,
                                accentColor = accentColor,
                                modifier    = Modifier.weight(1f),
                                onClick     = { clickedItem -> // When user presses: Live Track it: gets GPS, uploads location to Firebase
                                    when (clickedItem.label) {
                                        "Safe Route" -> onNavigate(Screen.SafeRoute)
                                        "Live Track" -> {

                                            if (
                                                ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                                ) == PackageManager.PERMISSION_GRANTED
                                            ) {

                                                isTracking = !isTracking
                                                if (isTracking) {

                                                    LocationUtils.getCurrentLocation(context) { lat, lng ->

                                                        val mapsLink =
                                                            "https://maps.google.com/?q=$lat,$lng"

                                                        val shareIntent = Intent().apply {

                                                            action = Intent.ACTION_SEND

                                                            putExtra(
                                                                Intent.EXTRA_TEXT,

                                                                """
                🚨 LIVE TRACKING ENABLED
                
                Track my live location:
                
                $mapsLink
                """.trimIndent()
                                                            )

                                                            type = "text/plain"

                                                            `package` = "com.whatsapp"
                                                        }

                                                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                                        try {

                                                            context.startActivity(shareIntent)

                                                        } catch (e: Exception) {

                                                            Toast.makeText(
                                                                context,
                                                                "WhatsApp not installed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }

                                                Toast.makeText(
                                                    context,
                                                    if (isTracking)
                                                        "Live tracking started"
                                                    else
                                                        "Live tracking stopped",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                if (isTracking) {

                                                    LocationUtils.getCurrentLocation(context) { lat, lng ->

                                                        val uid =
                                                            FirebaseAuth.getInstance()
                                                                .currentUser?.uid
                                                                ?: return@getCurrentLocation

                                                        val locationData = hashMapOf(
                                                            "latitude" to lat,
                                                            "longitude" to lng,
                                                            "timestamp" to System.currentTimeMillis()
                                                        )

                                                        FirebaseFirestore.getInstance()
                                                            .collection("live_locations")
                                                            .document(uid)
                                                            .set(locationData)
                                                    }
                                                }

                                            } else {

                                                activity?.let {

                                                    ActivityCompat.requestPermissions(
                                                        it,
                                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                                        100
                                                    )
                                                }
                                            }
                                        }
                                        "Network"    -> onNavigate(Screen.CrowdNetwork)
                                        "Flashlight" -> {
                                            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
                                            try {
                                                val cameraId = cameraManager.cameraIdList.firstOrNull {
                                                    cameraManager.getCameraCharacteristics(it)
                                                        .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                                                }
                                                cameraId?.let {
                                                    isFlashOn = !isFlashOn
                                                    cameraManager.setTorchMode(it, isFlashOn)
                                                }
                                            } catch (e: Exception) { e.printStackTrace() }
                                        }
                                        "Helpline" -> {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:112")
                                            }
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                context.startActivity(intent)
                                            } else {
                                                activity?.let {
                                                    ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.CALL_PHONE), 1)
                                                }
                                            }
                                        }
                                        "Record" -> {

                                            if (!audioRecorder.isCurrentlyRecording()) {

                                                audioRecorder.startRecording()

                                                Toast.makeText(
                                                    context,
                                                    "Emergency recording started",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            } else {

                                                audioRecorder.stopRecording()

                                                Toast.makeText(
                                                    context,
                                                    "Recording saved",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Safety Tip Banner ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF1B0A3E), Color(0xFF2D1060)))
                    )
                    .border(1.dp, PurpleAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Safety Tip",
                            color      = GoldAccent,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            "Share your live location with a trusted guardian when travelling at night.",
                            color      = WhiteHome.copy(alpha = 0.65f),
                            fontSize   = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─── SOS quick action button ──────────────────────────────────────────────────
@Composable
private fun SOSQuickAction(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(WhiteHome.copy(alpha = 0.06f))
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color     = WhiteHome.copy(alpha = 0.55f),
            fontSize  = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

// ─── Quick access card ────────────────────────────────────────────────────────
@Composable
private fun QuickAccessCard(
    item: QuickItem,
    bgColor: Color,
    accentColor: Color,
    onClick: (QuickItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable { onClick(item) }
            .padding(vertical = 18.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = accentColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            item.label,
            color = Color(0xFF1A0A3B),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0820)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}