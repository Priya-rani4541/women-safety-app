package com.example.womensafetyapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset

// ─── Colors (shared palette) ──────────────────────────────────────────────────
private val BgDarkLogin   = Color(0xFF1A0A3B)
private val PurpleHeader  = Color(0xFF3D1A8A)
private val PurpleDark    = Color(0xFF2A0E6B)
private val CardBg        = Color(0xFFFDF5FF)
private val PinkBtn       = Color(0xFFE8325A)
private val PurpleBtn     = Color(0xFF9B32D6)
private val InputBg       = Color(0xFFFFFFFF)
private val BorderColor   = Color(0xFFE8D5F5)
private val TextDark      = Color(0xFF1A0A3B)
private val TextGray      = Color(0xFF9B8BB0)
private val TextWhite     = Color(0xFFFFFFFF)
private val TextHint      = Color(0xFFBBABCC)
private val BlobPink      = Color(0xFFD946A8)
private val BlobPurple    = Color(0xFF7C3AED)

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit = {},
    onSignIn: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleSignIn: () -> Unit = {},
    onCreateAccount: () -> Unit = {}
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardBg)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Purple header section with blobs ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PurpleDark, PurpleHeader)
                    )
                )
        ) {
            // Blob circles decoration
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Top-left large blob
                drawCircle(
                    color  = BlobPurple.copy(alpha = 0.4f),
                    radius = 110.dp.toPx(),
                    center = Offset(-20.dp.toPx(), -20.dp.toPx())
                )
                // Center-right blob
                drawCircle(
                    color  = BlobPink.copy(alpha = 0.35f),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.8f, 60.dp.toPx())
                )
                // Bottom left small blob
                drawCircle(
                    color  = BlobPurple.copy(alpha = 0.25f),
                    radius = 55.dp.toPx(),
                    center = Offset(size.width * 0.3f, size.height * 0.9f)
                )
            }

            // Header text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 28.dp, bottom = 32.dp)
            ) {
                Text(
                    text       = "Welcome Back",
                    color      = TextWhite,
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = "Sign in to your SheShield account",
                    color    = TextWhite.copy(alpha = 0.80f),
                    fontSize = 14.sp
                )
            }
        }

        // ── White card body ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Email
            InputLabel("EMAIL ADDRESS")
            Spacer(Modifier.height(8.dp))
            ShieldTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = "your@email.com",
                icon          = Icons.Default.Email,
                keyboardType  = KeyboardType.Email
            )

            Spacer(Modifier.height(20.dp))

            // Password
            InputLabel("PASSWORD")
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

            Spacer(Modifier.height(10.dp))

            // Forgot password
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text       = "Forgot password?",
                    color      = PinkBtn,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(Modifier.height(28.dp))

            // Sign in button
            GradientButton(
                text    = "Sign In Securely",
                onClick = { onSignIn(email, password) }
            )

            Spacer(Modifier.height(20.dp))

            // Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    modifier  = Modifier.weight(1f),
                    color     = BorderColor,
                    thickness = 1.dp
                )
                Text(
                    text     = "  or continue with  ",
                    color    = TextGray,
                    fontSize = 12.sp
                )
                Divider(
                    modifier  = Modifier.weight(1f),
                    color     = BorderColor,
                    thickness = 1.dp
                )
            }

            Spacer(Modifier.height(20.dp))

            // Google sign in
            OutlinedButton(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = InputBg),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                // Google dot (simplified)
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4285F4), Color(0xFF34A853))
                            )
                        )
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text       = "Sign in with Google",
                    color      = TextDark,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(32.dp))

            // Create account link
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "New here? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text       = "Create account",
                    color      = PinkBtn,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onCreateAccount() }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Reusable sub-components ───────────────────────────────────────────────────

@Composable
private fun InputLabel(text: String) {
    Text(
        text          = text,
        color         = TextDark,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun ShieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePass: () -> Unit = {}
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = {
            Text(placeholder, color = TextHint, fontSize = 14.sp)
        },
        leadingIcon   = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon  = if (isPassword) ({
            IconButton(onClick = onTogglePass) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Lock else Icons.Default.Lock,
                    contentDescription = "Toggle password",
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }) else null,
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine      = true,
        modifier        = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape           = RoundedCornerShape(14.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PurpleBtn.copy(alpha = 0.6f),
            unfocusedBorderColor = BorderColor,
            focusedContainerColor   = InputBg,
            unfocusedContainerColor = InputBg,
            cursorColor             = PurpleBtn
        )
    )
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(PinkBtn, PurpleBtn)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            color      = TextWhite,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
