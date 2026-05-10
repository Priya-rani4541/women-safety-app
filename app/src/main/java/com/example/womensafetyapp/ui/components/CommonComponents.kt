package com.example.womensafetyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePass: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = Color(0xFFBBABCC), fontSize = 14.sp) },
        leadingIcon   = { Icon(icon, null, tint = Color(0xFF9B32D6).copy(alpha = 0.6f), modifier = Modifier.size(20.dp)) },
        trailingIcon  = {
            if (isPassword) {
                Text(
                    text     = if (showPassword) "HIDE" else "SHOW",
                    color    = Color(0xFFE8325A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 12.dp).clickable { onTogglePass() }
                )
            }
        },
        modifier      = Modifier.fillMaxWidth().height(56.dp),
        shape         = RoundedCornerShape(14.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor   = Color.White,
            unfocusedBorderColor    = Color(0xFFE8D5F5),
            focusedBorderColor      = Color(0xFF9B32D6).copy(alpha = 0.6f)
        ),
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions      = KeyboardOptions(keyboardType = keyboardType),
        singleLine           = true
    )
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFFE8325A), Color(0xFF9B32D6)))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            color      = Color.White,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
