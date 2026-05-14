package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import com.example.womensafetyapp.data.model.CommunityReport
import com.example.womensafetyapp.data.model.NetworkNode

class CrowdNetworkViewModel : ViewModel() {

    // -----------------------------
    // SELECTED NAV
    // -----------------------------

    var selectedNav by mutableStateOf("Network")
        private set

    // -----------------------------
    // NETWORK NODES
    // -----------------------------

    var nodes by mutableStateOf(

        listOf(

            NetworkNode(
                0.20f,
                0.18f,
                "#4CAF50",
                "G1"
            ),

            NetworkNode(
                0.55f,
                0.08f,
                "#7C3AED",
                "G2"
            ),

            NetworkNode(
                0.82f,
                0.22f,
                "#FF9800",
                "G3"
            ),

            NetworkNode(
                0.10f,
                0.50f,
                "#7C3AED",
                "G4"
            ),

            NetworkNode(
                0.50f,
                0.42f,
                "#E8325A",
                "You",
                true
            ),

            NetworkNode(
                0.78f,
                0.48f,
                "#4CAF50",
                "G5"
            ),

            NetworkNode(
                0.30f,
                0.70f,
                "#7C3AED",
                "G6"
            ),

            NetworkNode(
                0.68f,
                0.72f,
                "#FF9800",
                "G7"
            ),

            NetworkNode(
                0.90f,
                0.65f,
                "#7C3AED",
                "G8"
            )
        )
    )
        private set

    // -----------------------------
    // CONNECTIONS
    // -----------------------------

    var connections by mutableStateOf(

        listOf(

            Pair(0, 4),
            Pair(1, 4),
            Pair(2, 4),
            Pair(3, 4),

            Pair(4, 5),
            Pair(4, 6),
            Pair(4, 7),

            Pair(5, 8),

            Pair(1, 2),
            Pair(6, 7)
        )
    )
        private set

    // -----------------------------
    // COMMUNITY REPORTS
    // -----------------------------

    var reports by mutableStateOf(

        listOf(

            CommunityReport(
                "✅",
                "#4CAF50",
                "Well-lit area reported near Lodi Garden",
                "2 MIN AGO",
                "0.3KM AWAY"
            ),

            CommunityReport(
                "⚠️",
                "#FF9800",
                "Poor lighting reported on MG Road stretch",
                "8 MIN AGO",
                "0.7KM AWAY"
            ),

            CommunityReport(
                "🚨",
                "#E8325A",
                "Suspicious activity near Metro Station",
                "15 MIN AGO",
                "1.1KM AWAY"
            )
        )
    )
        private set

    // -----------------------------
    // NAVIGATION
    // -----------------------------

    fun onNavSelected(
        label: String
    ) {

        selectedNav = label
    }
}