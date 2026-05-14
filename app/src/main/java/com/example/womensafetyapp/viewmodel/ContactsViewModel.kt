package com.example.womensafetyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import com.example.womensafetyapp.data.model.Guardian
import com.example.womensafetyapp.data.model.Helpline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ContactsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    // -----------------------------
    // GUARDIANS
    // -----------------------------

    var guardians by mutableStateOf<List<Guardian>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadGuardians()
    }

    // -----------------------------
    // LOAD GUARDIANS
    // -----------------------------

    fun loadGuardians() {

        val uid = auth.currentUser?.uid ?: return

        isLoading = true

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .get()
            .addOnSuccessListener { result ->

                guardians =
                    result.documents.mapNotNull {

                        it.toObject(Guardian::class.java)
                    }

                isLoading = false
            }

            .addOnFailureListener {

                errorMessage = it.message

                isLoading = false
            }
    }

    // -----------------------------
    // ADD GUARDIAN
    // -----------------------------

    fun addGuardian(
        guardian: Guardian
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .add(guardian)

            .addOnSuccessListener {

                loadGuardians()
            }

            .addOnFailureListener {

                errorMessage = it.message
            }
    }

    // -----------------------------
    // DELETE GUARDIAN
    // -----------------------------

    fun deleteGuardian(
        documentId: String
    ) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .document(documentId)
            .delete()

            .addOnSuccessListener {

                loadGuardians()
            }

            .addOnFailureListener {

                errorMessage = it.message
            }
    }

    // -----------------------------
    // HELPLINES
    // -----------------------------

    val helplines = listOf(

        Helpline(
            "Police",
            "100",
            "🚓"
        ),

        Helpline(
            "Ambulance",
            "108",
            "🚑"
        ),

        Helpline(
            "Fire Service",
            "101",
            "🔥"
        ),

        Helpline(
            "Women Helpline",
            "1091",
            "👩"
        ),

        Helpline(
            "Child Helpline",
            "1098",
            "👶"
        )
    )
}