package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme

@Composable
fun ProfileScreen(onBack: () -> Unit = {}) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2B004D))
            .padding(16.dp)
    ) {

        Text("← Back", color = Color.White, modifier = Modifier.clickable { onBack() })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "My Shield Profile",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("👩", fontSize = 40.sp)
            Text("Riya Sharma", fontWeight = FontWeight.Bold)
            Text("riya@email.com")

            Spacer(modifier = Modifier.height(12.dp))

            Text("SHIELD PRO", color = Color.Magenta)
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingItem("Notifications")
        SettingItem("Location Sharing")
        SettingItem("Voice Trigger")
        SettingItem("Stealth Mode")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Logout", color = Color.Red)
    }
}

@Composable
fun SettingItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Switch(checked = true, onCheckedChange = {})
    }
}