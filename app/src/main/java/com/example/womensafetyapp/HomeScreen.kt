package com.example.womensafetyapp

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Colors ────────────────────────────────────────────────────────────────────
private val HomeBg        = Color(0xFF1A0A3B)
private val HeaderPurple  = Color(0xFF2D1060)
private val CardBgHome    = Color(0xFFF3EEF8)
private val SOSRed        = Color(0xFFD93025)
private val SOSRing1      = Color(0xFFE8325A).copy(alpha = 0.25f)
private val SOSRing2      = Color(0xFFE8325A).copy(alpha = 0.12f)
private val PinkBtn2      = Color(0xFFE8325A)
private val PurpleBtn2    = Color(0xFF9B32D6)
private val WhiteHome     = Color(0xFFFFFFFF)
private val TextDarkHome  = Color(0xFF1A0A3B)
private val TextGrayHome  = Color(0xFF9B8BB0)
private val NavActive     = Color(0xFFE8325A)
private val NavInactive   = Color(0xFF9B8BB0)
private val ShieldBlue    = Color(0xFF4A90D9)
private val GreenDot      = Color(0xFF4CAF50)

data class QuickItem(val emoji: String, val label: String)

@Composable
fun HomeScreen(
    onSOSTriggered: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val quickItems = listOf(
        QuickItem("🗺️", "Safe Route"),
        QuickItem("📍", "Live Track"),
        QuickItem("🤝", "Network"),
        QuickItem("🔦", "Flashlight"),
        QuickItem("🚨", "Helpline"),
        QuickItem("🎙️", "Record"),
    )

    var selectedNav by remember { mutableStateOf("Home") }

    // SOS pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "sos")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "p1"
    )
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.75f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "p2"
    )

    var sosPressed by remember { mutableStateOf(false) }
    val sosScale by animateFloatAsState(
        targetValue = if (sosPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "sosScale"
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = HomeBg,
                tonalElevation = 0.dp
            ) {
                listOf(
                    Pair("🏠", "Home"),
                    Pair("🗺️", "Route"),
                    Pair("👥", "Network"),
                    Pair("👤", "Profile")
                ).forEach { (emoji, label) ->
                    NavigationBarItem(
                        selected = selectedNav == label,
                        onClick  = { selectedNav = label; onNavigate(label) },
                        icon = {
                            Text(emoji, fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                label,
                                fontSize = 11.sp,
                                color = if (selectedNav == label) NavActive else NavInactive
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedTextColor = NavActive,
                            unselectedTextColor = NavInactive
                        )
                    )
                }
            }
        },
        containerColor = HomeBg
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
                        Brush.verticalGradient(
                            listOf(Color(0xFF3D1080), HomeBg)
                        )
                    )
                    .padding(start = 22.dp, end = 22.dp, top = 48.dp, bottom = 24.dp)
            ) {
                Column {
                    Text(
                        "THURSDAY, 11:42 PM",
                        color = WhiteHome.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Stay safe, Riya ",
                            color = WhiteHome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("🛡️", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "3 guardians are watching over you",
                        color = WhiteHome.copy(alpha = 0.70f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    // Shield Active pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WhiteHome.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GreenDot)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Shield Active · Home Zone",
                            color = WhiteHome,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── SOS Card ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBgHome)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Pulsing rings + SOS button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(200.dp)
                    ) {
                        // Outer ring
                        Box(
                            modifier = Modifier
                                .size((160 * pulse2).dp)
                                .clip(CircleShape)
                                .background(SOSRing2)
                        )
                        // Middle ring
                        Box(
                            modifier = Modifier
                                .size((130 * pulse1).dp)
                                .clip(CircleShape)
                                .background(SOSRing1)
                        )
                        // SOS Button
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(sosScale)
                                .clip(CircleShape)
                                .background(SOSRed)
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
                                    color = WhiteHome,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    "HOLD 3s",
                                    color = WhiteHome.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Long press to activate emergency alert",
                        color = TextGrayHome,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Quick Access ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "QUICK ACCESS",
                    color = WhiteHome.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                // 2-column grid
                quickItems.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            QuickAccessCard(
                                item = item,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickAccessCard(item: QuickItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A1550))
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(item.emoji, fontSize = 26.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            item.label,
            color = WhiteHome.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0A3B)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
