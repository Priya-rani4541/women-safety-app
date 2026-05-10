package com.example.womensafetyapp

import androidx.compose.ui.graphics.Color

data class Guardian(
    val name: String = "",
    val phone: String = "",
    val relation: String = "",
    val avatarEmoji: String = "👩",
    val avatarColor: Color = Color(0xFFD946A8)
)

