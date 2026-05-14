package com.example.womensafetyapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Colors ────────────────────────────────────────────────────────────────────
private val BgDark       = Color(0xFF0D0620)
private val PinkAccent   = Color(0xFFE8325A)
private val CircleColor  = Color(0xFF3D2080)
private val WhiteText    = Color(0xFFFFFFFF)

@Composable
fun SheShieldSplashScreen(onFinished: () -> Unit = {}) {

    // ── Animate concentric rings ───────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "rings")

    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue  = 1.0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue  = 0.30f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )

    // ── Shield pulse ───────────────────────────────────────────────────────────
    val shieldPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue  = 1.05f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shieldPulse"
    )

    // ── Progress bar ───────────────────────────────────────────────────────────
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val steps = 100
        repeat(steps) {
            delay(20L)
            progress = (it + 1) / steps.toFloat()
        }
        delay(200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {

        // ── Concentric animated rings ──────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseRadius = size.minDimension * 0.35f

            listOf(1.0f, 0.75f, 0.50f, 0.28f).forEachIndexed { i, factor ->
                val radius = baseRadius * factor * ringScale
                drawCircle(
                    color  = CircleColor.copy(alpha = ringAlpha * (1f - i * 0.15f)),
                    radius = radius,
                    center = Offset(cx, cy),
                    style  = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Soft inner glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5B2EA6).copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = baseRadius * 0.50f * ringScale
                ),
                radius = baseRadius * 0.50f * ringScale,
                center = Offset(cx, cy)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(Modifier.weight(1f))

            // ── Shield icon ────────────────────────────────────────────────────
            Canvas(modifier = Modifier.size((100 * shieldPulse).dp)) {
                drawShieldIcon(this)
            }

            Spacer(Modifier.height(36.dp))

            // ── Brand name ─────────────────────────────────────────────────────
            Row {
                Text(
                    text       = "She",
                    color      = WhiteText,
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text       = "Shield",
                    color      = PinkAccent,
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text          = "YOUR SAFETY, OUR PRIORITY",
                color         = WhiteText.copy(alpha = 0.55f),
                fontSize      = 11.sp,
                letterSpacing = 3.sp,
                fontWeight    = FontWeight.Medium,
                textAlign     = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            // ── Loading bar ────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(160.dp)
                        .height(3.dp)
                ) {
                    // Track
                    drawRoundRect(
                        color        = WhiteText.copy(alpha = 0.15f),
                        cornerRadius = CornerRadius(8f),
                        size         = size
                    )
                    // Fill
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PinkAccent, Color(0xFFAA44FF))
                        ),
                        cornerRadius = CornerRadius(8f),
                        size = Size(
                            width  = size.width * progress,
                            height = size.height
                        )
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text          = "INITIALIZING SHIELD...",
                    color         = WhiteText.copy(alpha = 0.38f),
                    fontSize      = 10.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

// ── Draw shield path ───────────────────────────────────────────────────────────
private fun drawShieldIcon(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height

        val shieldPath = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.22f)
            lineTo(w, h * 0.58f)
            cubicTo(w, h * 0.82f, w * 0.72f, h * 0.96f, w * 0.5f, h)
            cubicTo(w * 0.28f, h * 0.96f, 0f, h * 0.82f, 0f, h * 0.58f)
            lineTo(0f, h * 0.22f)
            close()
        }

        drawPath(
            path  = shieldPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE8325A), Color(0xFF9B32D6))
            )
        )

        // Female symbol ♀
        val cx = w * 0.5f
        val cy = h * 0.45f
        val r  = w * 0.18f

        drawCircle(
            color  = Color.White.copy(alpha = 0.9f),
            radius = r,
            center = Offset(cx, cy),
            style  = Stroke(width = w * 0.045f)
        )
        drawLine(
            color       = Color.White.copy(alpha = 0.9f),
            start       = Offset(cx, cy + r),
            end         = Offset(cx, cy + r + w * 0.22f),
            strokeWidth = w * 0.045f,
            cap         = StrokeCap.Round
        )
        val crossY = cy + r + w * 0.135f
        drawLine(
            color       = Color.White.copy(alpha = 0.9f),
            start       = Offset(cx - w * 0.12f, crossY),
            end         = Offset(cx + w * 0.12f, crossY),
            strokeWidth = w * 0.045f,
            cap         = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0620)
@Composable
fun SplashPreview() {
    SheShieldSplashScreen()
}