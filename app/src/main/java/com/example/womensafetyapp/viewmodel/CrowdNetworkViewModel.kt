package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.womensafetyapp.data.model.CommunityReport
import com.example.womensafetyapp.data.model.NetworkNode

class CrowdNetworkViewModel : ViewModel() {
    private val _selectedNav = mutableStateOf("Network")
    val selectedNav: State<String> = _selectedNav

    private val _nodes = mutableStateOf(
        listOf(
            NetworkNode(0.20f, 0.18f, Color(0xFF4CAF50), "G1"),
            NetworkNode(0.55f, 0.08f, Color(0xFF7C3AED), "G2"),
            NetworkNode(0.82f, 0.22f, Color(0xFFFF9800), "G3"),
            NetworkNode(0.10f, 0.50f, Color(0xFF7C3AED), "G4"),
            NetworkNode(0.50f, 0.42f, Color(0xFFE8325A), "You", isYou = true),
            NetworkNode(0.78f, 0.48f, Color(0xFF4CAF50), "G5"),
            NetworkNode(0.30f, 0.70f, Color(0xFF7C3AED), "G6"),
            NetworkNode(0.68f, 0.72f, Color(0xFFFF9800), "G7"),
            NetworkNode(0.90f, 0.65f, Color(0xFF7C3AED), "G8"),
        )
    )
    val nodes: State<List<NetworkNode>> = _nodes

    private val _connections = mutableStateOf(
        listOf(
            Pair(0, 4), Pair(1, 4), Pair(2, 4), Pair(3, 4),
            Pair(4, 5), Pair(4, 6), Pair(4, 7), Pair(5, 8),
            Pair(1, 2), Pair(6, 7)
        )
    )
    val connections: State<List<Pair<Int, Int>>> = _connections

    private val _reports = mutableStateOf(
        listOf(
            CommunityReport("✅", Color(0xFF4CAF50), "Well-lit area reported near Lodi Garden", "2 MIN AGO", "0.3KM AWAY"),
            CommunityReport("⚠️", Color(0xFFFF9800), "Poor lighting reported on MG Road stretch", "8 MIN AGO", "0.7KM AWAY"),
            CommunityReport("🚨", Color(0xFFE8325A), "Suspicious activity near Metro Station", "15 MIN AGO", "1.1KM AWAY"),
        )
    )
    val reports: State<List<CommunityReport>> = _reports

    fun onNavSelected(label: String) {
        _selectedNav.value = label
    }
}
