package com.example.womensafetyapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.data.model.Guardian
import com.example.womensafetyapp.data.model.Helpline
import com.example.womensafetyapp.viewmodel.ContactsViewModel

// ─── Colors ────────────────────────────────────────────────────────────────────
private val ECBg          = Color(0xFFF3EEF8)
private val ECHeaderBg    = Color(0xFF3D1080)
private val ECWhite       = Color(0xFFFFFFFF)
private val ECTextDark    = Color(0xFF1A0A3B)
private val ECTextGray    = Color(0xFF9B8BB0)
private val ECCardBg      = Color(0xFFFFFFFF)
private val ECPink        = Color(0xFFE8325A)
private val ECPurple      = Color(0xFF9B32D6)
private val ECGreen       = Color(0xFF4CAF50)
private val ECBorder      = Color(0xFFEEE0FF)
private val ECLabelColor  = Color(0xFF7B5EA7)

@Composable
fun EmergencyContactsScreen(
    onBack: () -> Unit = {},
    onAddGuardian: () -> Unit = {},
    viewModel: ContactsViewModel = viewModel()
) {
    val guardians by viewModel.guardians
    val helplines by viewModel.helplines

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ECBg)
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A0E6B), ECHeaderBg)
                    )
                )
                .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ECWhite.copy(alpha = 0.80f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Back",
                        color = ECWhite.copy(alpha = 0.80f),
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Emergency Contacts",
                    color = ECWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Your guardian network",
                    color = ECWhite.copy(alpha = 0.70f),
                    fontSize = 14.sp
                )
            }
        }

        // ── Scrollable body ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Guardians label
            Text(
                "GUARDIANS (${guardians.size}/5)",
                color = ECLabelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(10.dp))

            // Guardian cards
            guardians.forEach { guardian ->
                GuardianCard(guardian = guardian)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(14.dp))

            // Helplines label
            Text(
                "EMERGENCY HELPLINES",
                color = ECLabelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(10.dp))

            helplines.forEach { helpline ->
                HelplineCard(helpline = helpline)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Add Guardian button ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(listOf(ECPink, ECPurple))
                )
                .clickable { onAddGuardian() }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "+ Add Guardian",
                color = ECWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GuardianCard(guardian: Guardian) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ECCardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(guardian.avatarColor, guardian.avatarColor.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(guardian.avatarEmoji, fontSize = 22.sp)
        }

        Spacer(Modifier.width(14.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                guardian.name,
                color = ECTextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                "${guardian.relation} · ${guardian.phone}",
                color = ECTextGray,
                fontSize = 12.sp
            )
        }

        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContactActionBtn(emoji = "📞")
            ContactActionBtn(emoji = "💬")
        }
    }
}

@Composable
private fun ContactActionBtn(emoji: String) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0E8FF)),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 16.sp)
    }
}

@Composable
private fun HelplineCard(helpline: Helpline) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ECCardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(helpline.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(helpline.emoji, fontSize = 22.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                helpline.name,
                color = ECTextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(helpline.number, color = ECTextGray, fontSize = 12.sp)
        }

        // Call button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(ECGreen),
            contentAlignment = Alignment.Center
        ) {
            Text("📞", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyContactsPreview() {
    EmergencyContactsScreen()
}
