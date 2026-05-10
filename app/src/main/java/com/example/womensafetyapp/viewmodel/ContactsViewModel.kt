package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.womensafetyapp.data.model.Guardian
import com.example.womensafetyapp.data.model.Helpline

class ContactsViewModel : ViewModel() {
    private val _guardians = mutableStateOf(
        listOf(
            Guardian("Meera Sharma", "Mother", "+91 98765 43210", "👩", Color(0xFFD946A8)),
            Guardian("Rajesh Kumar", "Father", "+91 98765 12345", "👨", Color(0xFF4CAF50)),
            Guardian("Anjali Verma", "Best Friend", "+91 99887 65432", "👩", Color(0xFFE8A020)),
        )
    )
    val guardians: State<List<Guardian>> = _guardians

    private val _helplines = mutableStateOf(
        listOf(
            Helpline("Police", "100", "🚓", Color(0xFFE3F0FF)),
            Helpline("Ambulance", "108", "🚑", Color(0xFFFFE8E8)),
            Helpline("Fire Service", "101", "🔥", Color(0xFFFFF3E0)),
            Helpline("Women Helpline", "1091", "👩", Color(0xFFF3E5F5)),
            Helpline("Child Helpline", "1098", "👶", Color(0xFFE8F5E9)),
            Helpline("Pregnancy Medic", "102", "🏥", Color(0xFFE1F5FE)),
            Helpline("Legal Aid", "15100", "⚖️", Color(0xFFFFF9C4)),
        )
    )
    val helplines: State<List<Helpline>> = _helplines
}
