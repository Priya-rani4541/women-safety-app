package com.example.womensafetyapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CNBg     = Color(0xFF0D0620)
private val CNWhite  = Color(0xFFFFFFFF)
private val CNGray   = Color(0xFF9B8BB0)
private val CNPurple = Color(0xFF7C3AED)
private val CNGreen  = Color(0xFF4CAF50)
private val CNPink   = Color(0xFFE8325A)
private val CNOrange = Color(0xFFFF9800)
private val CNCard   = Color(0xFF1E0E40)
private val CNNav    = Color(0xFF160830)

data class NetworkNode(val x: Float, val y: Float, val color: Color, val label: String, val isYou: Boolean = false)
data class CommunityReport(val icon: String, val color: Color, val text: String, val time: String, val dist: String)

@Composable
fun CrowdNetworkScreen(
    onNavigate: (String) -> Unit = {}
) {
    var selectedNav by remember { mutableStateOf("Network") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "nodePulse"
    )

    val nodes = listOf(
        NetworkNode(0.20f, 0.18f, CNGreen,  "G1"),
        NetworkNode(0.55f, 0.08f, CNPurple, "G2"),
        NetworkNode(0.82f, 0.22f, CNOrange, "G3"),
        NetworkNode(0.10f, 0.50f, CNPurple, "G4"),
        NetworkNode(0.50f, 0.42f, CNPink,   "You", isYou = true),
        NetworkNode(0.78f, 0.48f, CNGreen,  "G5"),
        NetworkNode(0.30f, 0.70f, CNPurple, "G6"),
        NetworkNode(0.68f, 0.72f, CNOrange, "G7"),
        NetworkNode(0.90f, 0.65f, CNPurple, "G8"),
    )

    val connections = listOf(
        Pair(0, 4), Pair(1, 4), Pair(2, 4), Pair(3, 4),
        Pair(4, 5), Pair(4, 6), Pair(4, 7), Pair(5, 8),
        Pair(1, 2), Pair(6, 7)
    )

    val reports = listOf(
        CommunityReport("✅", CNGreen,  "Well-lit area reported near Lodi Garden", "2 MIN AGO", "0.3KM AWAY"),
        CommunityReport("⚠️", CNOrange, "Poor lighting reported on MG Road stretch", "8 MIN AGO", "0.7KM AWAY"),
        CommunityReport("🚨", CNPink,   "Suspicious activity near Metro Station", "15 MIN AGO", "1.1KM AWAY"),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CNNav, tonalElevation = 0.dp) {
                listOf(Pair("🏠","Home"), Pair("🗺️","Route"), Pair("👥","Network"), Pair("👤","Profile")).forEach { (emoji, label) ->
                    NavigationBarItem(
                        selected = selectedNav == label,
                        onClick = { selectedNav = label; onNavigate(label) },
                        icon = { Text(emoji, fontSize = 20.sp) },
                        label = { Text(label, fontSize = 11.sp, color = if (selectedNav == label) CNPink else CNGray) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                }
            }
        },
        containerColor = CNBg
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF2A0E6B), CNBg)))
                    .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Crowd Network", color = CNWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("🌐", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("247 shields active near you", color = CNGray, fontSize = 13.sp)
                }
            }

            // Network map canvas
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp).padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp)).background(Color(0xFF120428))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height
                    // Draw connections
                    connections.forEach { (a, b) ->
                        val nodeA = nodes[a]; val nodeB = nodes[b]
                        drawLine(
                            color = CNPurple.copy(alpha = 0.30f),
                            start = Offset(nodeA.x * w, nodeA.y * h),
                            end   = Offset(nodeB.x * w, nodeB.y * h),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                    // Draw nodes
                    nodes.forEach { node ->
                        val cx = node.x * w; val cy = node.y * h
                        val r  = if (node.isYou) 22.dp.toPx() else 14.dp.toPx()
                        if (node.isYou) {
                            drawCircle(node.color.copy(alpha = 0.20f), radius = r * pulse * 1.5f, center = Offset(cx, cy))
                        }
                        drawCircle(node.color, radius = r, center = Offset(cx, cy))
                        if (node.isYou) {
                            drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(cx, cy))
                        }
                    }
                }
                // "You" label
                Box(
                    modifier = Modifier.offset(
                        x = (nodes[4].x * 1f * 300).dp - 16.dp,
                        y = (nodes[4].y * 1f * 260).dp + 18.dp
                    )
                ) {
                    Text("You", color = CNWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NetworkStatCard("247",   "Active Shields", CNWhite,   Modifier.weight(1f))
                NetworkStatCard("1.2km", "Nearest\nShield", CNWhite,  Modifier.weight(1f))
                NetworkStatCard("HIGH",  "Safety Level",   CNGreen,   Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Community Reports
            Text(
                "COMMUNITY REPORTS",
                color = CNGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(10.dp))

            reports.forEach { report ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)).background(CNCard)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                            .background(report.color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) { Text(report.icon, fontSize = 16.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(report.text, color = CNWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(3.dp))
                        Text("${report.time} · ${report.dist}", color = CNGray, fontSize = 10.sp, letterSpacing = 0.5.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NetworkStatCard(value: String, label: String, valueColor: Color, modifier: Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(Color(0xFF1E0E40)).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color(0xFF9B8BB0), fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0620)
@Composable
fun CrowdNetworkPreview() { CrowdNetworkScreen() }