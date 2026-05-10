package com.example.womensafetyapp.data.model

import androidx.compose.ui.graphics.Color

data class NetworkNode(val x: Float, val y: Float, val color: Color, val label: String, val isYou: Boolean = false)
data class CommunityReport(val icon: String, val color: Color, val text: String, val time: String, val dist: String)
