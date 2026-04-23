package com.example.womensafetyapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────────────────────
private val BgReg        = Color(0xFFFDF5FF)
private val PurpleDarkReg= Color(0xFF2A0E6B)
private val PurpleHeaderR= Color(0xFF3D1A8A)
private val PinkBtnReg   = Color(0xFFE8325A)
private val PurpleBtnReg = Color(0xFF9B32D6)
private val InputBgReg   = Color(0xFFFFFFFF)
private val BorderReg    = Color(0xFFE8D5F5)
private val TextDarkReg  = Color(0xFF1A0A3B)
private val TextGrayReg  = Color(0xFF9B8BB0)
private val TextWhiteReg = Color(0xFFFFFFFF)
private val TextHintReg  = Color(0xFFBBABCC)
private val BlobPinkReg  = Color(0xFFD946A8)
private val BlobPurpleReg= Color(0xFF7C3AED)

@Composable
fun RegisterScreen(
    onCreateAccount: (name: String, phone: String, email: String, password: String) -> Unit = { _, _, _, _ -> },
    onSignIn: () -> Unit = {}
) {
    var fullName  by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgReg)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Purple header with blobs ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PurpleDarkReg, PurpleHeaderR)
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Top-right blob
                drawCircle(
                    color  = BlobPurpleReg.copy(alpha = 0.38f),
                    radius = 100.dp.toPx(),
                    center = Offset(size.width + 20.dp.toPx(), -20.dp.toPx())
                )
                // Middle-left blob
                drawCircle(
                    color  = BlobPinkReg.copy(alpha = 0.30f),
                    radius = 75.dp.toPx(),
                    center = Offset(-10.dp.toPx(), 70.dp.toPx())
                )
                // Bottom-right smaller blob
                drawCircle(
                    color  = BlobPurpleReg.copy(alpha = 0.22f),
                    radius = 50.dp.toPx(),
                    center = Offset(size.width * 0.65f, size.height * 0.88f)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 28.dp, bottom = 28.dp)
            ) {
                Text(
                    text       = "Create Account",
                    color      = TextWhiteReg,
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text     = "Join the SheShield network",
                    color    = TextWhiteReg.copy(alpha = 0.80f),
                    fontSize = 14.sp
                )
            }
        }

        // ── Form body ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgReg)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(28.dp))

            // Full Name
            RegInputLabel("FULL NAME")
            Spacer(Modifier.height(8.dp))
            ShieldTextField(
                value         = fullName,
                onValueChange = { fullName = it },
                placeholder   = "Your full name",
                icon          = Icons.Default.Person,
                keyboardType  = KeyboardType.Text
            )

            Spacer(Modifier.height(18.dp))

            // Phone Number
            RegInputLabel("PHONE NUMBER")
            Spacer(Modifier.height(8.dp))
            ShieldTextField(
                value         = phone,
                onValueChange = { phone = it },
                placeholder   = "+91 00000 00000",
                icon          = Icons.Default.Phone,
                keyboardType  = KeyboardType.Phone
            )

            Spacer(Modifier.height(18.dp))

            // Email Address
            RegInputLabel("EMAIL ADDRESS")
            Spacer(Modifier.height(8.dp))
            ShieldTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = "your@email.com",
                icon          = Icons.Default.Email,
                keyboardType  = KeyboardType.Email
            )

            Spacer(Modifier.height(18.dp))

            // Password
            RegInputLabel("PASSWORD")
            Spacer(Modifier.height(8.dp))
            ShieldTextField(
                value           = password,
                onValueChange   = { password = it },
                placeholder     = "••••••••",
                icon            = Icons.Default.Lock,
                isPassword      = true,
                showPassword    = showPass,
                onTogglePass    = { showPass = !showPass }
            )

            Spacer(Modifier.height(32.dp))

            // Create account button
            GradientButton(
                text    = "Create My Shield",
                onClick = { onCreateAccount(fullName, phone, email, password) }
            )

            Spacer(Modifier.height(24.dp))

            // Sign in link
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text     = "Already have an account? ",
                    color    = TextGrayReg,
                    fontSize = 14.sp
                )
                Text(
                    text       = "Sign In",
                    color      = PinkBtnReg,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onSignIn() }
                )
            }

            Spacer(Modifier.height(36.dp))
        }
    }
}

@Composable
private fun RegInputLabel(text: String) {
    Text(
        text          = text,
        color         = TextDarkReg,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
