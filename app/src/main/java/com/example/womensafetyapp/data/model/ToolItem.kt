package com.example.womensafetyapp.data.model

import androidx.compose.ui.graphics.Color

data class ToolItem(
    val emoji: String,
    val title: String,
    val desc: String,
    val badge: String? = null,
    val badgeColor: Color = Color.Red
)
