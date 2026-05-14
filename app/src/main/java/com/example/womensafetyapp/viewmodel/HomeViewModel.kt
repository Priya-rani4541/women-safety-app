package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import com.example.womensafetyapp.data.model.QuickItem

class HomeViewModel : ViewModel() {

    // -----------------------------
    // SELECTED BOTTOM NAV
    // -----------------------------

    var selectedNav by mutableStateOf("Home")
        private set

    // -----------------------------
    // QUICK ACTION ITEMS
    // -----------------------------

    var quickItems by mutableStateOf(

        listOf(

            QuickItem(
                "🗺️",
                "Safe Route"
            ),

            QuickItem(
                "📍",
                "Live Track"
            ),

            QuickItem(
                "🤝",
                "Network"
            ),

            QuickItem(
                "🔦",
                "Flashlight"
            ),

            QuickItem(
                "🚨",
                "Helpline"
            ),

            QuickItem(
                "🎙️",
                "Record"
            )
        )
    )
        private set

    // -----------------------------
    // NAVIGATION UPDATE
    // -----------------------------

    fun onNavSelected(
        label: String
    ) {

        selectedNav = label
    }
}