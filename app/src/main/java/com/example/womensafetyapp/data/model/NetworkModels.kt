package com.example.womensafetyapp.data.model

data class NetworkNode(

    val x: Float,

    val y: Float,

    val color: String,

    val label: String,

    val isYou: Boolean = false
)

data class CommunityReport(

    val emoji: String,

    val color: String,

    val message: String,

    val time: String,

    val distance: String
)