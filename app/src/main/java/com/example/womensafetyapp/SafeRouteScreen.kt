package com.example.womensafetyapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SRBg       = Color(0xFF1A0A3B)
private val SRHeader   = Color(0xFF3D1080)
private val SRWhite    = Color(0xFFFFFFFF)
private val SRGray     = Color(0xFF9B8BB0)
private val SRCard     = Color(0xFFFFFFFF)
private val SRMapBg    = Color(0xFFB8D8E8)
private val SRGreen    = Color(0xFF4CAF50)
private val SRRed      = Color(0xFFE8325A)
private val SROrange   = Color(0xFFFF9800)
private val SRPink     = Color(0xFFE8325A)
private val SRPurple   = Color(0xFF9B32D6)
private val SRTextDark = Color(0xFF1A0A3B)

@Composable
fun SafeRouteScreen(onBack: () -> Unit = {}) {
    var origin      by remember { mutableStateOf("Connaught Place, Delhi") }
    var destination by remember { mutableStateOf("") }

    // Animated dashed line progress
    val infiniteTransition = rememberInfiniteTransition(label = "route")
    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 40f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "dash"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(SRBg)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF2A0E6B), SRHeader)))
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = SRWhite.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Back", color = SRWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text("Safe Route Finder", color = SRWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("AI-powered safety scoring", color = SRWhite.copy(alpha = 0.7f), fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            // Map canvas
            Box(
                modifier = Modifier.fillMaxWidth().height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SRMapBg)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height
                    // Grid lines
                    val gridColor = Color(0xFFAAC8D8)
                    val cols = 6; val rows = 5
                    for (i in 0..cols) {
                        val x = w * i / cols
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1.dp.toPx())
                    }
                    for (i in 0..rows) {
                        val y = h * i / rows
                        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1.dp.toPx())
                    }
                    // Danger zone (pink circle)
                    drawCircle(
                        color = Color(0xFFE8325A).copy(alpha = 0.18f),
                        radius = 70.dp.toPx(),
                        center = Offset(w * 0.22f, h * 0.55f)
                    )
                    drawCircle(
                        color = Color(0xFFE8325A).copy(alpha = 0.10f),
                        radius = 90.dp.toPx(),
                        center = Offset(w * 0.22f, h * 0.55f)
                    )
                    // Safe zone (green circle)
                    drawCircle(
                        color = Color(0xFF4CAF50).copy(alpha = 0.18f),
                        radius = 55.dp.toPx(),
                        center = Offset(w * 0.72f, h * 0.38f)
                    )
                    // Dashed green route line
                    val startX = w * 0.18f; val startY = h * 0.80f
                    val endX   = w * 0.78f; val endY   = h * 0.22f
                    val dashLen = 14.dp.toPx(); val gapLen = 8.dp.toPx()
                    val dx = endX - startX; val dy = endY - startY
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                    val ux = dx / dist; val uy = dy / dist
                    var traveled = dashOffset.dp.toPx() % (dashLen + gapLen)
                    while (traveled < dist) {
                        val s = Offset(startX + ux * traveled, startY + uy * traveled)
                        val eD = (traveled + dashLen).coerceAtMost(dist)
                        val e = Offset(startX + ux * eD, startY + uy * eD)
                        drawLine(Color(0xFF4CAF50), s, e, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                        traveled += dashLen + gapLen
                    }
                    // Origin dot (red)
                    drawCircle(Color(0xFFE8325A), radius = 9.dp.toPx(), center = Offset(startX, startY))
                    drawCircle(Color.White,       radius = 4.dp.toPx(), center = Offset(startX, startY))
                    // Dest dot (green)
                    drawCircle(Color(0xFF4CAF50), radius = 10.dp.toPx(), center = Offset(endX, endY))
                    drawCircle(Color.White,       radius = 4.5.dp.toPx(), center = Offset(endX, endY))
                }
                // Safe Route Active pill
                Box(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp)).background(SRWhite)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(SRGreen))
                        Spacer(Modifier.width(6.dp))
                        Text("Safe Route Active", color = SRTextDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Origin field
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)).background(SRCard)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(SRRed))
                Spacer(Modifier.width(10.dp))
                Text(origin, color = SRTextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(8.dp))

            // Destination field
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(SRGreen))
                        Spacer(Modifier.width(8.dp))
                        Text("Destination", color = SRGray, fontSize = 14.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SRCard,
                    focusedContainerColor   = SRCard,
                    unfocusedBorderColor    = Color(0xFFE8D5F5),
                    focusedBorderColor      = SRPurple.copy(alpha = 0.6f)
                ),
                singleLine = true
            )

            Spacer(Modifier.height(14.dp))

            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot(SRGreen,  "Safe zones")
                LegendDot(SRRed,   "Avoid zones")
                LegendDot(SROrange,"Caution")
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("94%",   "Safety Score", SRGreen,  Modifier.weight(1f))
                StatCard("3.2 km","Distance",     SRTextDark, Modifier.weight(1f))
                StatCard("12 min","Est. Time",    SRTextDark, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Navigate button
            Box(
                modifier = Modifier.fillMaxWidth().height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(SRPink, SRPurple)))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text("Navigate Safely →", color = SRWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(5.dp))
        Text(label, color = Color(0xFF9B8BB0), fontSize = 12.sp)
    }
}

@Composable
private fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2A1550)).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color(0xFF9B8BB0), fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0A3B)
@Composable
fun SafeRoutePreview() { SafeRouteScreen() }