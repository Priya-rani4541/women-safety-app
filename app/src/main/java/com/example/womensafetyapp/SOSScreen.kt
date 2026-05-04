package com.example.womensafetyapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import kotlinx.coroutines.delay

// ─── Colors ────────────────────────────────────────────────────────────────────
private val SOSBg         = Color(0xFF0A0010)
private val SOSRedMain    = Color(0xFFD93025)
private val SOSRedGlow    = Color(0xFFE8325A)
private val SOSCountdown  = Color(0xFFD93025)
private val WhiteSOS      = Color(0xFFFFFFFF)
private val TextGraySOS   = Color(0xFF9B8BB0)
private val CancelBg      = Color(0xFF2A1A3B)
private val AvatarPurple  = Color(0xFF7C3AED)
private val AvatarPink    = Color(0xFFD946A8)

@Composable
fun SOSScreen(onCancel: () -> Unit = {},
              onAlertSent: () -> Unit = {}) {

    var countdown by remember { mutableIntStateOf(3) }
    var isSending by remember { mutableStateOf(false) }

    // Countdown logic
    LaunchedEffect(Unit) {
//        while (countdown > 0) {
//            delay(1000L)
//            countdown--
//        }
//        isSending = true

        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        onAlertSent()
    }

    // Pulsing ring animations
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.75f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "r1"
    )
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.65f, targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "r2"
    )
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "r3"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SOSBg),
        contentAlignment = Alignment.Center
    ) {

        // Background glow canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val base = size.minDimension * 0.42f

            // Glow rings
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
                    color  = SOSRedGlow.copy(alpha = alpha * 0.5f),
                    radius = base * scale,
                    center = Offset(cx, cy),
                    style  = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.12f))

            // ── "ALERT ACTIVATING IN" label ────────────────────────────────────
            Text(
                "ALERT ACTIVATING IN",
                color = WhiteSOS.copy(alpha = 0.50f),
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            // ── Countdown number ───────────────────────────────────────────────
            val countText = if (isSending) "" else "%02d".format(countdown)
            Text(
                countText,
                color = SOSCountdown,
                fontSize = 88.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp
            )

            Spacer(Modifier.weight(0.05f))

            // ── SOS Circle ────────────────────────────────────────────────────
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

            // ── Location being shared ──────────────────────────────────────────
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
                "Notifying your 3 guardians",
                color = TextGraySOS,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Guardian avatars ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair("👩", AvatarPink),
                    Pair("👨", AvatarPurple),
                    Pair("👩", Color(0xFFE8A020))
                ).forEach { (emoji, color) ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(listOf(color, color.copy(alpha = 0.7f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 26.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Cancel button ─────────────────────────────────────────────────
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
