package com.example.womensafetyapp

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SSBg      = Color(0xFF1A0410)
private val SSRed     = Color(0xFFD93025)
private val SSWhite   = Color(0xFFFFFFFF)
private val SSGray    = Color(0xFF9B8BB0)
private val SSPink    = Color(0xFFE8325A)
private val SSPurple  = Color(0xFF7C3AED)
private val SSCancel  = Color(0xFF2A1530)

@Composable
fun SOSSentScreen(
    onCancelAlert: () -> Unit = {}) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "sosSentPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(SSBg)) {
        // Background radial glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SSRed.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.22f),
                    radius = size.minDimension * 0.55f
                ),
                radius = size.minDimension * 0.55f,
                center = Offset(size.width / 2f, size.height * 0.22f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // "EMERGENCY ALERT SENT" banner
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚠️", fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "EMERGENCY ALERT SENT",
                    color = SSGray,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Pulsing SOS circle
            Box(
                modifier = Modifier.size((90 * pulse).dp).clip(CircleShape).background(SSRed),
                contentAlignment = Alignment.Center
            ) {
                Text("SOS", color = SSWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(28.dp))

            Text("Help Is Coming", color = SSWhite, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Text(
                "Your location and emergency details have\nbeen sent to your guardians and nearby\nsafety network.",
                color = SSGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // Location box
            Box(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)).background(Color(0xFF2A1030))
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("📍  YOUR LOCATION", color = SSGray, fontSize = 10.sp, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Connaught Place, New Delhi", color = SSWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("28.6315° N, 77.2167° E", color = SSGray, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Guardian avatars
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Pair("👩", SSPink), Pair("👨", SSPurple), Pair("👩", Color(0xFFE8A020))).forEach { (emoji, color) ->
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(color.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) { Text(emoji, fontSize = 24.sp) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Meera, Rajesh & Anjali notified", color = SSGray, fontSize = 12.sp)

            Spacer(Modifier.height(28.dp))

            // Call Police button
            Box(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(SSPink, Color(0xFFFF6060))))
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📞", fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Call Police (100)", color = SSWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Cancel button
            Box(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().height(54.dp)
                    .clip(RoundedCornerShape(14.dp)).background(SSCancel)
                    .clickable { onCancelAlert() },
                contentAlignment = Alignment.Center
            ) {
                Text("I'm Safe – Cancel Alert", color = SSWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))

            Text("Recording audio · Sharing live location", color = SSGray.copy(alpha = 0.6f), fontSize = 11.sp)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0410)
@Composable
fun SOSSentPreview() { SOSSentScreen() }