package com.example.womensafetyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.viewmodel.ProfileViewModel

private val ProfileBg       = Color(0xFFF3EEF8)
private val ProfileHeaderBg = Color(0xFF3D1080)
private val ProfileWhite    = Color(0xFFFFFFFF)
private val ProfileTextDark = Color(0xFF1A0A3B)
private val ProfileTextGray = Color(0xFF9B8BB0)
private val ProfilePink     = Color(0xFFE8325A)
private val ProfilePurple   = Color(0xFF9B32D6)

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val notifications by viewModel.notificationsEnabled
    val locationSharing by viewModel.locationSharingEnabled
    val voiceTrigger by viewModel.voiceTriggerEnabled
    val stealthMode by viewModel.stealthModeEnabled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A0E6B), ProfileHeaderBg)
                    )
                )
                .padding(top = 48.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.ArrowBack,
                        null,
                        tint = ProfileWhite.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Back", color = ProfileWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "My Shield Profile",
                    color = ProfileWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Profile Section ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(ProfileBg)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDE7F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👩", fontSize = 48.sp)
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Priya Sharma",
                    color = ProfileTextDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    "priya@email.com · +91 98765 00000",
                    color = ProfileTextGray,
                    fontSize = 14.sp
                )
                
                Spacer(Modifier.height(12.dp))
                
                // Shield Pro Badge
                Surface(
                    color = ProfilePink,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "SHIELD PRO",
                        color = ProfileWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Settings ───────────────────────────────────────────────────────
            SettingSwitchItem("🔔", "Notifications", notifications) { viewModel.toggleNotifications(it) }
            SettingSwitchItem("📍", "Location Sharing", locationSharing) { viewModel.toggleLocationSharing(it) }
            SettingSwitchItem("🎙️", "Voice Trigger", voiceTrigger) { viewModel.toggleVoiceTrigger(it) }
            SettingSwitchItem("👁️", "Stealth Mode", stealthMode) { viewModel.toggleStealthMode(it) }

            Spacer(Modifier.height(16.dp))

            // ── Menu ───────────────────────────────────────────────────────────
            SettingMenuItem("📥", "Share App")
            SettingMenuItem("⭐", "Rate SheShield")
            SettingMenuItem("🚪", "Logout", isLogout = true, onClick = onLogout)
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingSwitchItem(icon: String, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ProfileWhite)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(14.dp))
            Text(title, color = ProfileTextDark, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ProfileWhite,
                checkedTrackColor = ProfilePink,
                uncheckedThumbColor = ProfileWhite,
                uncheckedTrackColor = ProfileTextGray.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingMenuItem(icon: String, title: String, isLogout: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ProfileWhite)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(14.dp))
            Text(
                title,
                color = if (isLogout) Color(0xFFD32F2F) else ProfileTextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        if (!isLogout) {
            Icon(
                androidx.compose.material.icons.Icons.Default.ChevronRight,
                null,
                tint = ProfileTextGray.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProfilePreview() { ProfileScreen() }
