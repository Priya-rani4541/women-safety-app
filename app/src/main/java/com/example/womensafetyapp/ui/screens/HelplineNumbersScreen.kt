package com.example.womensafetyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
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
import com.example.womensafetyapp.data.model.Helpline
import com.example.womensafetyapp.viewmodel.ContactsViewModel

private val HLBg       = Color(0xFFF3EEF8)
private val HLHeader   = Color(0xFF3D1080)
private val HLWhite    = Color(0xFFFFFFFF)
private val HLTextDark = Color(0xFF1A0A3B)
private val HLTextGray = Color(0xFF9B8BB0)
private val HLGreen    = Color(0xFF4CAF50)

@Composable
fun HelplineNumbersScreen(
    onBack: () -> Unit = {},
    viewModel: ContactsViewModel = viewModel()
) {
    val helplines by viewModel.helplines

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HLBg)
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A0E6B), HLHeader)
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
                        null,
                        tint = HLWhite.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Back", color = HLWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Helpline Numbers",
                    color = HLWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Official emergency contacts",
                    color = HLWhite.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        // ── List ───────────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(helplines) { helpline ->
                HelplineItem(helpline)
            }
        }
    }
}

@Composable
fun HelplineItem(helpline: Helpline) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HLWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(helpline.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(helpline.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    helpline.name,
                    color = HLTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    helpline.number,
                    color = HLTextGray,
                    fontSize = 13.sp
                )
            }
        }
        
        // Call button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(HLGreen)
                .clickable { /* Handle call */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Call, null, tint = HLWhite, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview
@Composable
fun HelplineNumbersPreview() { HelplineNumbersScreen() }
