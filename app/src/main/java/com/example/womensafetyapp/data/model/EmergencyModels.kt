package com.example.womensafetyapp.data.model

import androidx.compose.ui.graphics.Color

data class Guardian(
    val name: String,
    val relation: String,
    val phone: String,
    val avatarEmoji: String,
    val avatarColor: Color
)

data class Helpline(
    val name: String,
    val number: String,
    val emoji: String,
    val bgColor: Color
)
