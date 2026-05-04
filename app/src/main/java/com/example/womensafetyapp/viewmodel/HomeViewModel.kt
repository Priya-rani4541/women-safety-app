package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.womensafetyapp.data.model.QuickItem

class HomeViewModel : ViewModel() {
    private val _selectedNav = mutableStateOf("Home")
    val selectedNav: State<String> = _selectedNav

    private val _quickItems = mutableStateOf(
        listOf(
            QuickItem("🗺️", "Safe Route"),
            QuickItem("📍", "Live Track"),
            QuickItem("🤝", "Network"),
            QuickItem("🔦", "Flashlight"),
            QuickItem("🚨", "Helpline"),
            QuickItem("🎙️", "Record"),
        )
    )
    val quickItems: State<List<QuickItem>> = _quickItems

    fun onNavSelected(label: String) {
        _selectedNav.value = label
    }
}
