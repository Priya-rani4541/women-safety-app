package com.example.womensafetyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private val PrimaryPurple = Color(0xFF9333EA)
private val PrimaryPink = Color(0xFFE8325A)
private val BorderColor = Color(0xFFE8D5F5)
private val PlaceholderColor = Color(0xFFB7A6C8)

@Composable
fun ShieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = value,

            onValueChange = onValueChange,

            placeholder = {
                Text(
                    text = placeholder,
                    color = PlaceholderColor,
                    fontSize = 14.sp
                )
            },

            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryPurple.copy(alpha = 0.7f)
                )
            },

            trailingIcon = {

                if (isPassword) {

                    IconButton(
                        onClick = onTogglePassword
                    ) {

                        Icon(
                            imageVector =
                                if (showPassword)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,

                            contentDescription = null,
                            tint = PrimaryPurple.copy(alpha = 0.7f)
                        )
                    }
                }
            },

            singleLine = true,

            shape = RoundedCornerShape(16.dp),

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            visualTransformation =
                if (isPassword && !showPassword)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,

            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),

            colors = OutlinedTextFieldDefaults.colors(

                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,

                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = BorderColor,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

                cursorColor = PrimaryPurple
            )
        )

        if (errorMessage != null) {

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            PrimaryPink,
            PrimaryPurple
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (enabled)
                    gradient
                else
                    Brush.horizontalGradient(
                        listOf(
                            Color.Gray,
                            Color.DarkGray
                        )
                    )
            )
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onClick()
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}