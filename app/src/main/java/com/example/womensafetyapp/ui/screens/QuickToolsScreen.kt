package com.example.womensafetyapp.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

private val QTBg      = Color(0xFF1A0A3B)
private val QTHeader  = Color(0xFF3D1080)
private val QTWhite   = Color(0xFFFFFFFF)
private val QTCard    = Color(0xFFFFFFFF)
private val QTTextD   = Color(0xFF1A0A3B)
private val QTGray    = Color(0xFF9B8BB0)
private val QTRed     = Color(0xFFE8325A)
private val QTPurple  = Color(0xFF9B32D6)

data class ToolItem(
    val emoji: String,
    val title: String,
    val desc: String,
    val badge: String? = null,
    val badgeColor: Color = Color.Red
)

@Composable
fun QuickToolsScreen(
    onBack: () -> Unit = {},
    onFakeCall: () -> Unit = {}
) {
    val context = LocalContext.current
    var strobActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val tools = listOf(
        ToolItem("🎥", "Stealth Recorder",  "Record audio & video silently while screen is off", "LIVE", Color(0xFFE8325A)),
        ToolItem("🔦", "Strobe SOS",        "Flash light in SOS pattern to attract attention"),
        ToolItem("🎭", "Fake Call",         "Simulate an incoming call to exit situations",      "NEW",  Color(0xFF9B32D6)),
        ToolItem("📊", "Area Heat Map",     "View community-reported danger spots nearby"),
        ToolItem("🤝", "Safety Buddy",      "Match with nearby verified SheShield users"),
        ToolItem("🎙️", "Voice Trigger",    "Set a code word to instantly trigger SOS"),
    )

    Column(modifier = Modifier.fillMaxSize().background(QTBg)) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF2A0E6B), QTHeader)))
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onBack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = QTWhite.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Back", color = QTWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text("Quick Tools", color = QTWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Safety tools at your fingertips", color = QTWhite.copy(alpha = 0.7f), fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            tools.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { tool ->
                        ToolCard(
                            tool = tool,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (tool.title) {
                                    "Strobe SOS"   -> {
                                        strobActive = !strobActive
                                        if (strobActive) triggerStrobeSOS(context, scope) { strobActive = false }
                                    }
                                    "Fake Call"    -> onFakeCall()
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ToolCard(tool: ToolItem, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFFF8F0FF), Color(0xFFFDF5FF)))
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(tool.emoji, fontSize = 28.sp)
                tool.badge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tool.badgeColor)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(badge, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(tool.title, color = QTTextD, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(tool.desc, color = QTGray, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

private fun triggerStrobeSOS(context: Context, scope: CoroutineScope, onDone: () -> Unit) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
    val cameraId = try { cameraManager.cameraIdList.firstOrNull() } catch (e: Exception) { null } ?: return
    // SOS = ... --- ... (3 short, 3 long, 3 short)
    val sosTiming = listOf(200L, 200L, 200L, 600L, 600L, 600L, 200L, 200L, 200L)
    scope.launch(Dispatchers.IO) {
        repeat(3) {
            sosTiming.forEach { duration ->
                try {
                    cameraManager.setTorchMode(cameraId, true)
                    delay(duration)
                    cameraManager.setTorchMode(cameraId, false)
                    delay(100)
                } catch (_: Exception) {}
            }
            delay(1000)
        }
        withContext(Dispatchers.Main) { onDone() }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A0A3B)
@Composable
fun QuickToolsPreview() { QuickToolsScreen() }