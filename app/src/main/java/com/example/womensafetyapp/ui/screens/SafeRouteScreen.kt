package com.example.womensafetyapp.ui.screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ────────────────────────────────────────────────────────────────────
private val SRBg        = Color(0xFF0F0820)
private val SRHeaderTop = Color(0xFF2A0E6B)
private val SRWhite     = Color(0xFFFFFFFF)
private val SRGray      = Color(0xFF9B8BB0)
private val SRCard      = Color(0xFFFFFFFF)
private val SRDarkCard  = Color(0xFF1A0F2E)
private val SRMapBg     = Color(0xFFB8D4E8)
private val SRGreen     = Color(0xFF22C55E)
private val SRRed       = Color(0xFFE8325A)
private val SROrange    = Color(0xFFF59E0B)
private val SRPurple    = Color(0xFF9333EA)
private val SRTextDark  = Color(0xFF1A0A3B)
private val SRBorder    = Color(0xFFE8D5F5)

@Composable
fun SafeRouteScreen(
    onBack: () -> Unit = {}
) {
    var origin      by remember { mutableStateOf("Connaught Place, Delhi") }
    var destination by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "route")

    // Animated dash offset for moving route line
    val dashOffset by infiniteTransition.animateFloat(
        initialValue  = 0f, targetValue = 40f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label         = "dash"
    )

    // Pulsing safe zone
    val safeZonePulse by infiniteTransition.animateFloat(
        initialValue  = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "safeZone"
    )

    // Blinking dot on route
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue  = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label         = "dot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SRBg)
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(SRHeaderTop, Color(0xFF1A0A3B)))
                )
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Decorative circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(SRRed.copy(alpha = 0.08f))
            )
            Column {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Safe Route Finder",
                    color      = SRWhite,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "AI-powered safety scoring",
                    color    = SRWhite.copy(alpha = 0.60f),
                    fontSize = 13.sp
                )
            }
        }

        // ── Scrollable body ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ── Map Canvas ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(SRMapBg)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    val gridColor = Color(0xFFAAC4D8)
                    for (i in 0..7) {
                        val x = w * i / 7f
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1.dp.toPx())
                    }
                    for (i in 0..5) {
                        val y = h * i / 5f
                        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1.dp.toPx())
                    }

                    // Block buildings (light shapes for realism)
                    val buildingColor = Color(0xFFA0C0D8)
                    drawRect(buildingColor, Offset(w * 0.05f, h * 0.10f), androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.14f))
                    drawRect(buildingColor, Offset(w * 0.60f, h * 0.60f), androidx.compose.ui.geometry.Size(w * 0.10f, h * 0.18f))
                    drawRect(buildingColor, Offset(w * 0.42f, h * 0.12f), androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.10f))

                    // ── Danger zone ───────────────────────────────────────────
                    drawCircle(
                        color  = Color(0xFFE8325A).copy(alpha = 0.14f),
                        radius = 80.dp.toPx(),
                        center = Offset(w * 0.22f, h * 0.62f)
                    )
                    drawCircle(
                        color  = Color(0xFFE8325A).copy(alpha = 0.08f),
                        radius = 105.dp.toPx(),
                        center = Offset(w * 0.22f, h * 0.62f)
                    )
                    // Danger icon dot
                    drawCircle(Color(0xFFE8325A).copy(alpha = 0.6f), radius = 6.dp.toPx(), center = Offset(w * 0.22f, h * 0.62f))

                    // ── Safe zone ─────────────────────────────────────────────
                    drawCircle(
                        color  = Color(0xFF22C55E).copy(alpha = 0.16f),
                        radius = 62.dp.toPx() * safeZonePulse,
                        center = Offset(w * 0.75f, h * 0.32f)
                    )
                    drawCircle(
                        color  = Color(0xFF22C55E).copy(alpha = 0.08f),
                        radius = 85.dp.toPx() * safeZonePulse,
                        center = Offset(w * 0.75f, h * 0.32f)
                    )

                    // ── Dashed green route line ────────────────────────────────
                    val startX = w * 0.16f; val startY = h * 0.82f
                    val endX   = w * 0.80f; val endY   = h * 0.22f
                    val dashLen  = 14.dp.toPx()
                    val gapLen   = 7.dp.toPx()
                    val dx   = endX - startX; val dy = endY - startY
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                    val ux = dx / dist; val uy = dy / dist
                    var traveled = dashOffset.dp.toPx() % (dashLen + gapLen)
                    while (traveled < dist) {
                        val s  = Offset(startX + ux * traveled, startY + uy * traveled)
                        val eD = (traveled + dashLen).coerceAtMost(dist)
                        val e  = Offset(startX + ux * eD, startY + uy * eD)
                        drawLine(
                            color       = Color(0xFF22C55E),
                            start       = s,
                            end         = e,
                            strokeWidth = 3.5.dp.toPx(),
                            cap         = StrokeCap.Round
                        )
                        traveled += dashLen + gapLen
                    }

                    // Moving dot along route
                    val progress = (dashOffset / 40f).coerceIn(0f, 1f)
                    val movingX  = startX + (endX - startX) * progress
                    val movingY  = startY + (endY - startY) * progress
                    drawCircle(
                        color  = Color(0xFF22C55E).copy(alpha = dotAlpha),
                        radius = 7.dp.toPx(),
                        center = Offset(movingX, movingY)
                    )

                    // ── Origin dot (red) ──────────────────────────────────────
                    drawCircle(Color(0xFFE8325A).copy(alpha = 0.25f), radius = 16.dp.toPx(), center = Offset(startX, startY))
                    drawCircle(Color(0xFFE8325A), radius = 10.dp.toPx(), center = Offset(startX, startY))
                    drawCircle(Color.White,       radius = 4.5.dp.toPx(), center = Offset(startX, startY))

                    // ── Destination dot (green) ───────────────────────────────
                    drawCircle(Color(0xFF22C55E).copy(alpha = 0.25f), radius = 18.dp.toPx(), center = Offset(endX, endY))
                    drawCircle(Color(0xFF22C55E), radius = 11.dp.toPx(), center = Offset(endX, endY))
                    drawCircle(Color.White,       radius = 5.dp.toPx(),  center = Offset(endX, endY))
                }

                // Safe Route Active pill
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(SRWhite)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SRGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Safe Route Active",
                            color      = SRTextDark,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Safety score badge top right
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SRGreen)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("94% Safe", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Input fields ──────────────────────────────────────────────────

            // Origin
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(SRCard)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(SRRed)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        origin,
                        color      = SRTextDark,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Destination
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(SRCard)
            ) {
                OutlinedTextField(
                    value         = destination,
                    onValueChange = { destination = it },
                    placeholder   = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(SRGreen)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Destination", color = SRGray, fontSize = 14.sp)
                        }
                    },
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SRCard,
                        focusedContainerColor   = SRCard,
                        unfocusedBorderColor    = Color.Transparent,
                        focusedBorderColor      = SRPurple.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Legend ────────────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot(SRGreen,  "Safe zones")
                LegendDot(SRRed,    "Avoid zones")
                LegendDot(SROrange, "Caution")
            }

            Spacer(Modifier.height(16.dp))

            // ── Stats row ─────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EnhancedStatCard("94%",    "Safety Score", SRGreen,    Modifier.weight(1f))
                EnhancedStatCard("3.2 km", "Distance",     SRWhite,    Modifier.weight(1f))
                EnhancedStatCard("12 min", "Est. Time",    SRWhite,    Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Navigate button ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = SRRed, spotColor = SRRed)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(SRRed, SRPurple)))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Navigate Safely",
                        color      = SRWhite,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("→", color = SRWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Route info card ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SRDarkCard)
                    .border(1.dp, SRPurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "ℹ️  Route Info",
                        color      = SRWhite,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(10.dp))
                    RouteInfoRow("🚦", "Avoids 2 high-risk zones", SRGreen)
                    RouteInfoRow("💡", "Well-lit path throughout", SRGreen)
                    RouteInfoRow("🚔", "Police patrol zone on route", SRGray)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(5.dp))
        Text(label, color = SRGray, fontSize = 12.sp)
    }
}

@Composable
private fun EnhancedStatCard(value: String, label: String, valueColor: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A0F2E))
            .border(1.dp, Color(0xFF9333EA).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color      = valueColor,
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color     = SRGray,
            fontSize  = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RouteInfoRow(icon: String, text: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(10.dp))
        Text(text, color = textColor, fontSize = 12.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0820)
@Composable
fun SafeRoutePreview() { SafeRouteScreen() }