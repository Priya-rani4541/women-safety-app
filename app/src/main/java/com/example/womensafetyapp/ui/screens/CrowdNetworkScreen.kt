package com.example.womensafetyapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ────────────────────────────────────────────────────────────────────
private val CNBg      = Color(0xFF0D0620)
private val CNHeader  = Color(0xFF2A0E6B)
private val CNWhite   = Color(0xFFFFFFFF)
private val CNGray    = Color(0xFF9B8BB0)
private val CNPurple  = Color(0xFF7C3AED)
private val CNGreen   = Color(0xFF22C55E)
private val CNPink    = Color(0xFFE8325A)
private val CNOrange  = Color(0xFFF59E0B)
private val CNCard    = Color(0xFF1E0E40)
private val CNNav     = Color(0xFF160830)
private val CNMapBg   = Color(0xFF120428)

data class NetworkNode(
    val x: Float, val y: Float,
    val color: Color, val label: String,
    val isYou: Boolean = false
)

data class CommunityReport(
    val icon: String, val color: Color,
    val text: String, val time: String, val dist: String
)

@Composable
fun CrowdNetworkScreen(
    onNavigate: (String) -> Unit = {}
) {
    var selectedNav by remember { mutableStateOf("Network") }

    val infiniteTransition = rememberInfiniteTransition(label = "network")

    // Node pulse
    val nodePulse by infiniteTransition.animateFloat(
        initialValue  = 0.88f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "nodePulse"
    )

    // Line alpha shimmer
    val lineAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.20f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(1600, easing = EaseInOut), RepeatMode.Reverse),
        label         = "lineAlpha"
    )

    // Ripple scale
    val ripple by infiniteTransition.animateFloat(
        initialValue  = 0.5f, targetValue = 2.2f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label         = "ripple"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label         = "rippleAlpha"
    )

    val nodes = listOf(
        NetworkNode(0.20f, 0.18f, CNGreen,  "G1"),
        NetworkNode(0.55f, 0.08f, CNPurple, "G2"),
        NetworkNode(0.82f, 0.22f, CNOrange, "G3"),
        NetworkNode(0.10f, 0.50f, CNPurple, "G4"),
        NetworkNode(0.50f, 0.48f, CNPink,   "You", isYou = true),
        NetworkNode(0.78f, 0.48f, CNGreen,  "G5"),
        NetworkNode(0.30f, 0.72f, CNPurple, "G6"),
        NetworkNode(0.65f, 0.74f, CNOrange, "G7"),
        NetworkNode(0.90f, 0.62f, CNPurple, "G8"),
    )

    val connections = listOf(
        0 to 4, 1 to 4, 2 to 4, 3 to 4,
        4 to 5, 4 to 6, 4 to 7, 5 to 8,
        1 to 2, 6 to 7
    )

    val reports = listOf(
        CommunityReport("✅", CNGreen,  "Well-lit area reported near Lodi Garden",   "2 MIN AGO",  "0.3KM AWAY"),
        CommunityReport("⚠️", CNOrange, "Poor lighting reported on MG Road stretch",  "8 MIN AGO",  "0.7KM AWAY"),
        CommunityReport("🚨", CNPink,   "Suspicious activity near Metro Station",     "15 MIN AGO", "1.1KM AWAY"),
        CommunityReport("🚔", CNGreen,  "Police patrol active in Saket area",         "20 MIN AGO", "1.5KM AWAY"),
    )

    Scaffold(
        containerColor = CNBg,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF160828), CNNav)))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    listOf(
                        Triple("🏠", "Home",    "Home"),
                        Triple("🗺️", "Route",   "Route"),
                        Triple("👥", "Network", "Network"),
                        Triple("👤", "Profile", "Profile"),
                    ).forEach { (emoji, label, nav) ->
                        NavigationBarItem(
                            selected = selectedNav == label,
                            onClick  = { selectedNav = label; onNavigate(label) },
                            icon     = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 20.sp)
                                    if (selectedNav == label) {
                                        Spacer(Modifier.height(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(CNPink)
                                        )
                                    }
                                }
                            },
                            label    = {
                                Text(
                                    label,
                                    fontSize   = 10.sp,
                                    color      = if (selectedNav == label) CNPink else CNGray,
                                    fontWeight = if (selectedNav == label) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor      = Color.Transparent,
                                selectedTextColor   = CNPink,
                                unselectedTextColor = CNGray
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(CNHeader, CNBg)))
                    .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(CNPurple.copy(alpha = 0.12f))
                )
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Crowd Network",
                            color      = CNWhite,
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("🌐", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(CNGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "247 shields active near you",
                            color    = CNGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ── Network map ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(CNMapBg)
                    .border(1.dp, CNPurple.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background subtle grid
                    val gridC = CNPurple.copy(alpha = 0.05f)
                    for (i in 0..8) {
                        drawLine(gridC, Offset(w * i / 8f, 0f), Offset(w * i / 8f, h), 0.5.dp.toPx())
                        drawLine(gridC, Offset(0f, h * i / 8f), Offset(w, h * i / 8f), 0.5.dp.toPx())
                    }

                    // Draw connections
                    connections.forEach { (a, b) ->
                        val nodeA = nodes[a]; val nodeB = nodes[b]
                        val isConnectedToYou = a == 4 || b == 4
                        drawLine(
                            color = if (isConnectedToYou) CNPink.copy(alpha = lineAlpha)
                            else CNPurple.copy(alpha = lineAlpha * 0.7f),
                            start       = Offset(nodeA.x * w, nodeA.y * h),
                            end         = Offset(nodeB.x * w, nodeB.y * h),
                            strokeWidth = if (isConnectedToYou) 1.8.dp.toPx() else 1.2.dp.toPx()
                        )
                    }

                    // Draw nodes
                    nodes.forEach { node ->
                        val cx = node.x * w
                        val cy = node.y * h
                        val r  = if (node.isYou) 22.dp.toPx() else 13.dp.toPx()

                        if (node.isYou) {
                            // Ripple rings
                            drawCircle(node.color.copy(alpha = rippleAlpha * 0.25f), radius = r * ripple * 1.2f, center = Offset(cx, cy))
                            drawCircle(node.color.copy(alpha = 0.18f * nodePulse),   radius = r * 1.7f,          center = Offset(cx, cy))
                        } else {
                            // Subtle glow
                            drawCircle(node.color.copy(alpha = 0.15f), radius = r * 1.5f, center = Offset(cx, cy))
                        }

                        // Main node circle
                        drawCircle(node.color, radius = r, center = Offset(cx, cy))
                        drawCircle(Color.White.copy(alpha = 0.25f), radius = r, center = Offset(cx - r * 0.25f, cy - r * 0.25f))

                        if (node.isYou) {
                            drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(cx, cy))
                        } else {
                            // Small white center dot for non-you nodes
                            drawCircle(Color.White.copy(alpha = 0.5f), radius = 3.dp.toPx(), center = Offset(cx, cy))
                        }
                    }
                }

                // "You" label overlay
                val youNode = nodes[4]
                Box(
                    modifier = Modifier
                        .offset(
                            x = (youNode.x * 320).dp - 12.dp,
                            y = (youNode.y * 278).dp + 22.dp
                        )
                ) {
                    Text(
                        "You",
                        color      = CNWhite,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Live badge
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CNPink)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "LIVE",
                            color         = Color.White,
                            fontSize      = 10.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            fontFamily    = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Stats row ──────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NetworkStatCard("247",   "Active\nShields",  CNWhite,  Modifier.weight(1f))
                NetworkStatCard("1.2km", "Nearest\nShield",  CNWhite,  Modifier.weight(1f))
                NetworkStatCard("HIGH",  "Safety\nLevel",    CNGreen,  Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Community Reports ──────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "COMMUNITY REPORTS",
                    color         = CNGray,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily    = FontFamily.Monospace
                )
                Text(
                    "See all →",
                    color    = CNPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            reports.forEach { report ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CNCard)
                        .border(1.dp, report.color.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icon box with colored left accent
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(report.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(report.icon, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                report.text,
                                color      = CNWhite,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${report.time}  ·  ${report.dist}",
                                color         = CNGray,
                                fontSize      = 10.sp,
                                letterSpacing = 0.5.sp,
                                fontFamily    = FontFamily.Monospace
                            )
                        }
                        // Colored dot indicator on right
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(report.color)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Submit Report button ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CNCard)
                    .border(1.dp, CNPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable { }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Submit a Report",
                        color      = CNPurple,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun NetworkStatCard(value: String, label: String, valueColor: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E0E40))
            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color      = valueColor,
            fontSize   = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color     = CNGray,
            fontSize  = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0620)
@Composable
fun CrowdNetworkPreview() { CrowdNetworkScreen() }