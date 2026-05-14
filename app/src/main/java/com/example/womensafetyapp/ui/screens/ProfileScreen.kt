package com.example.womensafetyapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// ─── Color palette ─────────────────────────────────────────────────────────────
private val PBg           = Color(0xFFF3EEF8)
private val PHeader       = Color(0xFF1E0A2E)
private val PHeaderEnd    = Color(0xFF3B0764)
private val PCard         = Color(0xFFFFFFFF)
private val PPink         = Color(0xFFE8325A)
private val PPurple       = Color(0xFF9333EA)
private val PPurpleSoft   = Color(0xFF7C3AED)
private val PTextDark     = Color(0xFF1A0A3B)
private val PTextGray     = Color(0xFF9B8BB0)
private val PBorder       = Color(0xFFEEE0FF)
private val PSafe         = Color(0xFF22C55E)
private val PWarn         = Color(0xFFF59E0B)
private val PDanger       = Color(0xFFEF4444)
private val PNight        = Color(0xFF0F0A14)
private val PDarkCard     = Color(0xFF1E1030)

private val roseGradient   = Brush.linearGradient(listOf(PPink, PPurple))
private val headerGradient = Brush.verticalGradient(listOf(PHeader, PHeaderEnd))

// ─── Data class ────────────────────────────────────────────────────────────────
private data class UserProfile(
    val name: String         = "",
    val email: String        = "",
    val phone: String        = "",
    val joinedDate: String   = "",
    val guardianCount: Int   = 0,
    val isVerified: Boolean  = false,
    val photoUrl: String     = ""
)

// ─── Main screen ───────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    // ── State ──────────────────────────────────────────────────────────────────
    var profile        by remember { mutableStateOf(UserProfile()) }
    var isLoading      by remember { mutableStateOf(true) }
    var isDarkMode     by remember { mutableStateOf(false) }
    var voiceTrigger   by remember { mutableStateOf(true) }
    var autoSOS        by remember { mutableStateOf(false) }
    var liveLocation   by remember { mutableStateOf(true) }
    var shakeDetection by remember { mutableStateOf(false) }
    var fakeCall       by remember { mutableStateOf(true) }
    var notifications  by remember { mutableStateOf(true) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val bgColor   = if (isDarkMode) PNight   else PBg
    val cardColor = if (isDarkMode) PDarkCard else PCard
    val textColor = if (isDarkMode) Color.White else PTextDark
    val subColor  = if (isDarkMode) Color.White.copy(0.45f) else PTextGray

    // ── Image picker ───────────────────────────────────────────────────────────
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // ── Fetch Firestore data ───────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val firebaseUser = auth.currentUser

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val joined = doc.getTimestamp("createdAt")?.toDate()
                val formattedDate = joined?.let {
                    val sdf = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
                    "Joined ${sdf.format(it)}"
                } ?: "Joined recently"

                profile = UserProfile(
                    name        = doc.getString("name")  ?: firebaseUser?.displayName ?: "User",
                    email       = doc.getString("email") ?: firebaseUser?.email ?: "",
                    phone       = doc.getString("phone") ?: firebaseUser?.phoneNumber ?: "",
                    joinedDate  = formattedDate,
                    isVerified  = firebaseUser?.isEmailVerified ?: false,
                    photoUrl    = firebaseUser?.photoUrl?.toString() ?: ""
                )
            }

        // Fetch guardian count
        db.collection("users").document(uid).collection("contacts").get()
            .addOnSuccessListener { result ->
                profile = profile.copy(guardianCount = result.size())
                isLoading = false
            }
            .addOnFailureListener { isLoading = false }
    }

    // ── Logout confirmation dialog ─────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor   = cardColor,
            title = {
                Text("Logout", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Are you sure you want to logout from SheShield?",
                    color = subColor, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PDanger),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Text("Yes, Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape   = RoundedCornerShape(12.dp),
                    border  = BorderStroke(1.dp, PBorder)
                ) {
                    Text("Cancel", color = PPurple)
                }
            }
        )
    }

    // ── UI ─────────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
    ) {

        // ── 1. Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .padding(top = 52.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            // Decorative circle
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(PPink.copy(alpha = 0.12f))
            )
            Column {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.clickable { onBack() }
//                ) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
//                        tint = Color.White.copy(0.7f), modifier = Modifier.size(18.dp))
//                    Spacer(Modifier.width(6.dp))
//                    Text("Back", color = Color.White.copy(0.7f), fontSize = 14.sp)
//                }
//                Spacer(Modifier.height(12.dp))
                Spacer(Modifier.height(8.dp))

                Text(
                    "My Shield Profile",
                    color      = Color.White,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── 2. User Info Card ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-12).dp)
        ) {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with upload button
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier         = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(roseGradient)
                                .clickable { imagePicker.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model             = selectedImageUri,
                                    contentDescription = "Profile picture",
                                    contentScale      = ContentScale.Crop,
                                    modifier          = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else if (profile.photoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model             = profile.photoUrl,
                                    contentDescription = "Profile picture",
                                    contentScale      = ContentScale.Crop,
                                    modifier          = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Text("👩", fontSize = 36.sp, textAlign = TextAlign.Center)
                            }
                        }
                        // Camera icon overlay
                        Box(
                            modifier         = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(PPink)
                                .clickable { imagePicker.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Upload",
                                tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Name + verified badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = if (isLoading) "Loading..." else profile.name,
                            color      = textColor,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (profile.isVerified) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PSafe.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("✅ Verified", color = PSafe, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(profile.email, color = subColor, fontSize = 13.sp)
                    if (profile.phone.isNotEmpty()) {
                        Text(profile.phone, color = subColor, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(profile.joinedDate, color = subColor, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))
                    Divider(color = PBorder)
                    Spacer(Modifier.height(14.dp))

                    // Stats row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat("${profile.guardianCount}", "Guardians", cardColor, textColor, subColor)
                        VerticalDivider(modifier = Modifier.height(40.dp), color = PBorder)
                        ProfileStat("Active", "Shield", cardColor, textColor, subColor)
                        VerticalDivider(modifier = Modifier.height(40.dp), color = PBorder)
                        ProfileStat("PRO", "Plan", cardColor, textColor, subColor)
                    }
                }
            }
        }

        // offset spacer correction
        Spacer(Modifier.height((-8).dp))

        // ── 3. Safety Score Card ───────────────────────────────────────────────
        SectionCard(modifier = Modifier.padding(horizontal = 16.dp), cardColor = cardColor) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Safety Status", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PSafe.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("STRONG", color = PSafe, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(14.dp))
            SafetyStatusRow("SOS Active",             PSafe,  "✅", textColor)
            SafetyStatusRow("${profile.guardianCount} Trusted Guardians", PSafe, "✅", textColor)
            SafetyStatusRow("Voice Trigger Enabled",  if (voiceTrigger) PSafe else PWarn,  if (voiceTrigger) "✅" else "⚠️", textColor)
            SafetyStatusRow("Live Location Sharing",  if (liveLocation) PSafe else PDanger, if (liveLocation) "✅" else "❌", textColor)
            SafetyStatusRow("Shake Detection",        if (shakeDetection) PSafe else PTextGray, if (shakeDetection) "✅" else "○", textColor)
        }

        Spacer(Modifier.height(14.dp))

        // ── 4. Emergency Contacts Shortcut ────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(roseGradient)
                .clickable { onNavigateToContacts() }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("👥  Manage Guardians", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${profile.guardianCount}/5 guardians connected",
                        color = Color.White.copy(0.75f), fontSize = 12.sp
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── 5. Emergency Settings (Toggles) ───────────────────────────────────
        SectionCard(modifier = Modifier.padding(horizontal = 16.dp), cardColor = cardColor) {
            Text("Emergency Settings", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            ToggleRow("🎙️  Voice Trigger",   "Say code word to trigger SOS", voiceTrigger,   textColor, subColor) { voiceTrigger = it }
            ToggleRow("🆘  Auto SOS",        "Auto-send after countdown",     autoSOS,        textColor, subColor) { autoSOS = it }
            ToggleRow("📍  Live Location",   "Share location with guardians", liveLocation,   textColor, subColor) { liveLocation = it }
            ToggleRow("📳  Shake Detection", "Shake phone to trigger SOS",    shakeDetection, textColor, subColor) { shakeDetection = it }
            ToggleRow("🎭  Fake Call",       "Simulate incoming call",        fakeCall,       textColor, subColor) { fakeCall = it }
            ToggleRow("🔔  Notifications",   "Alert & reminder notifications",notifications,  textColor, subColor) { notifications = it }
        }

        Spacer(Modifier.height(14.dp))

        // ── 6. Recent Activity ─────────────────────────────────────────────────
        SectionCard(modifier = Modifier.padding(horizontal = 16.dp), cardColor = cardColor) {
            Text("Recent Activity", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            ActivityRow("🧪", "SOS tested",              "Yesterday",  PSafe,   textColor, subColor)
            ActivityRow("📍", "Live tracking enabled",   "2 days ago", PSafe,   textColor, subColor)
            ActivityRow("👥", "Guardian added",          "3 days ago", PPurple, textColor, subColor)
            ActivityRow("🔐", "Account verified",        "Last week",  PSafe,   textColor, subColor)
            ActivityRow("🛡️", "Shield Pro activated",   "Last week",  PWarn,   textColor, subColor)
        }

        Spacer(Modifier.height(14.dp))

        // ── 7. App Settings ────────────────────────────────────────────────────
        SectionCard(modifier = Modifier.padding(horizontal = 16.dp), cardColor = cardColor) {
            Text("App Settings", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            ToggleRow("🌙  Dark Mode", "Switch to dark theme", isDarkMode, textColor, subColor) { isDarkMode = it }
            Spacer(Modifier.height(8.dp))
            AppSettingRow("📤  Share App",      "Invite friends to SheShield", textColor, subColor)
            AppSettingRow("⭐  Rate SheShield", "Leave us a review",           textColor, subColor)
            AppSettingRow("💬  Feedback",       "Report a bug or suggestion",  textColor, subColor)
            AppSettingRow("ℹ️  About",          "Version 1.0.0",               textColor, subColor)
        }

        Spacer(Modifier.height(14.dp))

        // ── 8. Logout Button ───────────────────────────────────────────────────
        Button(
            onClick  = { showLogoutDialog = true },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(54.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PDanger),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null,
                tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(36.dp))
    }
}

// ─── Reusable sub-components ───────────────────────────────────────────────────

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    cardColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun ProfileStat(value: String, label: String, cardColor: Color, textColor: Color, subColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = PPink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = subColor, fontSize = 11.sp)
    }
}

@Composable
private fun SafetyStatusRow(label: String, color: Color, icon: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(10.dp))
        Text(label, color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    textColor: Color,
    subColor: Color,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = subColor,  fontSize = 11.sp)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = PPink,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD0C4E0)
            )
        )
    }
}

@Composable
private fun ActivityRow(
    icon: String,
    title: String,
    time: String,
    dotColor: Color,
    textColor: Color,
    subColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(dotColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(time,  color = subColor,  fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Composable
private fun AppSettingRow(title: String, subtitle: String, textColor: Color, subColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = subColor,  fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = subColor, modifier = Modifier.size(18.dp))
    }
}