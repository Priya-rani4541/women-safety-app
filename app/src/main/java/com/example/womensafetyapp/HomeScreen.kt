//package com.example.womensafetyapp
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.hardware.camera2.CameraManager
//import android.net.Uri
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import kotlinx.coroutines.delay
//import android.Manifest
//import androidx.compose.ui.platform.LocalConfiguration
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//
//// ─── Colors ────────────────────────────────────────────────────────────────────
//private val HomeBg        = Color(0xFF1A0A3B)
//private val HeaderPurple  = Color(0xFF2D1060)
//private val CardBgHome    = Color(0xFFF3EEF8)
//private val SOSRed        = Color(0xFFD93025)
//private val SOSRing1      = Color(0xFFE8325A).copy(alpha = 0.25f)
//private val SOSRing2      = Color(0xFFE8325A).copy(alpha = 0.12f)
//private val PinkBtn2      = Color(0xFFE8325A)
//private val PurpleBtn2    = Color(0xFF9B32D6)
//private val WhiteHome     = Color(0xFFFFFFFF)
//private val TextDarkHome  = Color(0xFF1A0A3B)
//private val TextGrayHome  = Color(0xFF9B8BB0)
//private val NavActive     = Color(0xFFE8325A)
//private val NavInactive   = Color(0xFF9B8BB0)
//private val ShieldBlue    = Color(0xFF4A90D9)
//private val GreenDot      = Color(0xFF4CAF50)
//
//data class QuickItem(val emoji: String, val label: String)
//
//@Composable
//fun HomeScreen(
//    onSOSTriggered: () -> Unit = {},
//    onNavigate: (Screen) -> Unit = {}
//) {
//    val context = LocalContext.current
//    val quickItems = listOf(
//        QuickItem("🗺️", "Safe Route"),
//        QuickItem("📍", "Live Track"),
//        QuickItem("🤝", "Network"),
//        QuickItem("🔦", "Flashlight"),
//        QuickItem("🚨", "Helpline"),
//        QuickItem("🎙️", "Record"),
//    )
//
//    var selectedNav by remember { mutableStateOf("Home") }
//
//    // SOS pulse animation
//    val infiniteTransition = rememberInfiniteTransition(label = "sos")
//    val pulse1 by infiniteTransition.animateFloat(
//        initialValue = 0.85f, targetValue = 1.15f,
//        animationSpec = infiniteRepeatable(
//            tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse
//        ), label = "p1"
//    )
//    val pulse2 by infiniteTransition.animateFloat(
//        initialValue = 0.75f, targetValue = 1.25f,
//        animationSpec = infiniteRepeatable(
//            tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse
//        ), label = "p2"
//    )
//
//    var sosPressed by remember { mutableStateOf(false) }
//    val sosScale by animateFloatAsState(
//        targetValue = if (sosPressed) 0.93f else 1f,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
//        label = "sosScale"
//    )
//    var isFlashOn by remember { mutableStateOf(false) }
//    // toggle flash light
//
//    val activity = context as? Activity
//
//    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
//    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//
//
//    val auth = FirebaseAuth.getInstance()
//    val db = FirebaseFirestore.getInstance()
//
//    var userName by remember {
//        mutableStateOf("User")
//    }
//
//    LaunchedEffect(Unit) {
//
//        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
//
//        db.collection("users")
//            .document(uid)
//            .get()
//            .addOnSuccessListener { document ->
//
//                userName = document.getString("name") ?: "User"
//            }
//    }
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar(
//                containerColor = HomeBg,
//                tonalElevation = 0.dp
//            ) {
//                listOf(
//                    Pair("🏠", "Home"),
//                    Pair("🗺️", "Route"),
//                    Pair("👥", "Network"),
//                    Pair("👤", "Profile")
//                ).forEach { (emoji, label) ->
//                    NavigationBarItem(
//                        selected = selectedNav == label,
//                        onClick = {
//                            selectedNav = label
//                            when (label) {
//                                "Home" -> onNavigate(Screen.HOME)
//                                "Route" -> onNavigate(Screen.SAFE_ROUTE)
//                                "Network" -> onNavigate(Screen.CROWD_NETWORK)
//                                "Profile" -> onNavigate(Screen.PROFILE)
//                            }
//                        },
//                        icon = {
//                            Text(emoji, fontSize = 20.sp)
//                        },
//                        label = {
//                            Text(
//                                label,
//                                fontSize = 11.sp,
//                                color = if (selectedNav == label) NavActive else NavInactive
//                            )
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            indicatorColor = Color.Transparent,
//                            selectedTextColor = NavActive,
//                            unselectedTextColor = NavInactive
//                        )
//                    )
//                }
//            }
//        },
//        containerColor = HomeBg
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(rememberScrollState())
//        ) {
//            // ── Header ─────────────────────────────────────────────────────────
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        Brush.verticalGradient(
//                            listOf(Color(0xFF3D1080), HomeBg)
//                        )
//                    )
//                    .padding(start = 22.dp, end = 22.dp, top = 40.dp, bottom = 24.dp)
//            ) {
//                Column {
//                    Text(
//                        "THURSDAY, 11:42 PM",
//                        color = WhiteHome.copy(alpha = 0.55f),
//                        fontSize = 11.sp,
//                        letterSpacing = 1.5.sp
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Text(
//                            text ="Stay safe, $userName 🛡️",
//                            color = WhiteHome,
//                            fontSize = 28.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text("🛡️", fontSize = 22.sp)
//                    }
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        "3 guardians are watching over you",
//                        color = WhiteHome.copy(alpha = 0.70f),
//                        fontSize = 13.sp
//                    )
//                    Spacer(Modifier.height(14.dp))
//                    // Shield Active pill
//                    Row(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(WhiteHome.copy(alpha = 0.12f))
//                            .padding(horizontal = 14.dp, vertical = 7.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(8.dp)
//                                .clip(CircleShape)
//                                .background(GreenDot)
//                        )
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            "Shield Active · Home Zone",
//                            color = WhiteHome,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//                }
//            }
//
//            // ── SOS Card ───────────────────────────────────────────────────────
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .clip(RoundedCornerShape(24.dp))
//                    .background(CardBgHome)
//                    .padding(vertical = 32.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    // Pulsing rings + SOS button
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier.size(screenWidth * 0.6f)
//                    ) {
//                        // Outer ring
//                        Box(
//                            modifier = Modifier
//                                .size((160 * pulse2).dp)
//                                .clip(CircleShape)
//                                .background(SOSRing2)
//                        )
//                        // Middle ring
//                        Box(
//                            modifier = Modifier
//                                .size((130 * pulse1).dp)
//                                .clip(CircleShape)
//                                .background(SOSRing1)
//                        )
//                        // SOS Button
//                        Box(
//                            modifier = Modifier
//                                .size(screenWidth * 0.32f)
//                                .scale(sosScale)
//                                .clip(CircleShape)
//                                .background(SOSRed)
//                                .pointerInput(Unit) {
//                                    detectTapGestures(
//                                        onPress = {
//                                            sosPressed = true
//                                            val released = tryAwaitRelease()
//                                            sosPressed = false
//                                            if (released) onSOSTriggered()
//                                        }
//                                    )
//                                },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text(
//                                    "SOS",
//                                    color = WhiteHome,
//                                    fontSize = 28.sp,
//                                    fontWeight = FontWeight.ExtraBold
//                                )
//                                Text(
//                                    "HOLD 3s",
//                                    color = WhiteHome.copy(alpha = 0.85f),
//                                    fontSize = 11.sp,
//                                    letterSpacing = 1.sp
//                                )
//                            }
//                        }
//                    }
//
//                    Spacer(Modifier.height(16.dp))
//                    Text(
//                        "Long press to activate emergency alert",
//                        color = TextGrayHome,
//                        fontSize = 13.sp,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(24.dp))
//
//            // ── Quick Access ───────────────────────────────────────────────────
//            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//                Text(
//                    "QUICK ACCESS",
//                    color = WhiteHome.copy(alpha = 0.65f),
//                    fontSize = 11.sp,
//                    letterSpacing = 2.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(Modifier.height(12.dp))
//
//                // 2-column grid
//                quickItems.chunked(2).forEach { rowItems ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        rowItems.forEach { item ->
//
//                            QuickAccessCard(
//                                item = item,
//                                modifier = Modifier.weight(1f),
//                                onClick = { item ->
//                                    when (item.label) {
//
//                                        "Safe Route" -> onNavigate(Screen.SAFE_ROUTE)
//
//                                        "Live Track" -> {
//                                            // TODO: start live tracking (location sharing)
//                                        }
//
//                                        "Network" -> onNavigate(Screen.CROWD_NETWORK)
//
//                                        "Flashlight" -> {
////                                            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
////                                            val cameraId = cameraManager.cameraIdList[0]
////
////                                            isFlashOn = !isFlashOn
////                                            cameraManager.setTorchMode(cameraId, isFlashOn)
//                                            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//                                            try {
//                                                val cameraId = cameraManager.cameraIdList.firstOrNull {
//                                                    cameraManager.getCameraCharacteristics(it)
//                                                        .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
//                                                }
//
//                                                cameraId?.let {
//                                                    isFlashOn = !isFlashOn
//                                                    cameraManager.setTorchMode(it, isFlashOn)
//                                                }
//
//                                            } catch (e: Exception) {
//                                                e.printStackTrace()
//                                            }
//                                        }
//
//                                        "Helpline" -> {
//                                            val intent = Intent(Intent.ACTION_CALL).apply {
//                                                data = Uri.parse("tel:112") // or 100
//                                            }
//
//                                            if (ContextCompat.checkSelfPermission(
//                                                    context,
//                                                    Manifest.permission.CALL_PHONE
//                                                ) == PackageManager.PERMISSION_GRANTED
//                                            ) {
//                                                context.startActivity(intent)
//                                            } else {
//                                                activity?.let {
//                                                    ActivityCompat.requestPermissions(
//                                                        it,
//                                                        arrayOf(Manifest.permission.CALL_PHONE),
//                                                        1
//                                                    )
//                                                }
//                                            }
//                                        }
//
//                                        "Record" -> {
//                                            // TODO: start audio/video recording
//                                        }
//                                    }
//                                }
//                            )
//                        }
//                    }
//                    Spacer(Modifier.height(10.dp))
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//private fun QuickAccessCard(
//    item: QuickItem,
//    onClick: (QuickItem) -> Unit,
//    modifier: Modifier = Modifier
//){    Column(
//        modifier = modifier
//            .clip(RoundedCornerShape(16.dp))
//            .background(Color(0xFF2A1550))
//            .clickable { onClick(item) }
//            .padding(vertical = 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(item.emoji, fontSize = 34.sp)
//        Spacer(Modifier.height(8.dp))
//        Text(
//            item.label,
//            color = WhiteHome.copy(alpha = 0.85f),
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Medium,
//            textAlign = TextAlign.Center
//        )
//    }
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFF1A0A3B)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}


package com.example.womensafetyapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import android.Manifest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

data class QuickItem(val emoji: String, val label: String)

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

    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    var userName       by remember { mutableStateOf("User") }
    var guardianCount  by remember { mutableStateOf(0) }
    var selectedNav    by remember { mutableStateOf("Home") }
    var isFlashOn      by remember { mutableStateOf(false) }
    var sosPressed     by remember { mutableStateOf(false) }

    val quickItems = listOf(
        QuickItem("🗺️", "Safe Route"),
        QuickItem("📍", "Live Track"),
        QuickItem("🤝", "Network"),
        QuickItem("🔦", "Flashlight"),
        QuickItem("🚨", "Helpline"),
        QuickItem("🎙️", "Record"),
    )

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
                        Triple("🏠", "Home",    Screen.HOME),
                        Triple("🗺️", "Route",   Screen.SAFE_ROUTE),
                        Triple("👥", "Network", Screen.CROWD_NETWORK),
                        Triple("👤", "Profile", Screen.PROFILE),
                    ).forEach { (emoji, label, screen) ->
                        NavigationBarItem(
                            selected = selectedNav == label,
                            onClick  = { selectedNav = label; onNavigate(screen) },
                            icon     = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 20.sp)
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
                            label    = {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    color    = if (selectedNav == label) NavActive else NavInactive,
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
                            "THURSDAY, 11:42 PM",
                            color         = WhiteHome.copy(alpha = 0.40f),
                            fontSize      = 10.sp,
                            letterSpacing = 1.5.sp,
                            fontFamily    = FontFamily.Monospace
                        )
                        Box(
                            modifier         = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(WhiteHome.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔔", fontSize = 15.sp)
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
                                                val released = tryAwaitRelease()
                                                sosPressed = false
                                                if (released) onSOSTriggered()
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
                            SOSQuickAction("📞", "Call\nPolice")
                            SOSQuickAction("📍", "Share\nLocation")
                            SOSQuickAction("📳", "Alert\nVibrate")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

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
                                onClick     = { clickedItem ->
                                    when (clickedItem.label) {
                                        "Safe Route" -> onNavigate(Screen.SAFE_ROUTE)
                                        "Live Track" -> { /* TODO */ }
                                        "Network"    -> onNavigate(Screen.CROWD_NETWORK)
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
                                            val intent = Intent(Intent.ACTION_CALL).apply {
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
                                        "Record" -> { /* TODO */ }
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
private fun SOSQuickAction(emoji: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(WhiteHome.copy(alpha = 0.06f))
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
        modifier            = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable { onClick(item) }
            .padding(vertical = 18.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(item.emoji, fontSize = 24.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            item.label,
            color      = Color(0xFF1A0A3B),
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0820)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}