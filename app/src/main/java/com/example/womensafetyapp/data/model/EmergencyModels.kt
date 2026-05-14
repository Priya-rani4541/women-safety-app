package com.example.womensafetyapp.data.model

data class Guardian(

    val name: String = "",

    val relation: String = "",

    val phone: String = "",

    val avatarEmoji: String = "👩",

    val avatarColor: String = "#9333EA"
)

data class Helpline(

    val name: String = "",

    val number: String = "",

    val emoji: String = "",

    val bgColor: String = "#E3F0FF"
)