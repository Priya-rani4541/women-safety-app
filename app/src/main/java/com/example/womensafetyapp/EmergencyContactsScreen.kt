package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun EmergencyContactScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var guardianName by remember { mutableStateOf("") }
    var guardianPhone by remember { mutableStateOf("") }
    var guardianRelation by remember { mutableStateOf("") }

    var guardians by remember {
        mutableStateOf(listOf<Guardian>())
    }

    val uid = auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .get()
            .addOnSuccessListener { result ->

                guardians = result.documents.mapNotNull {
                    it.toObject(Guardian::class.java)
                }
            }
    }

    Column {

        // input fields here
        OutlinedTextField(
            value = guardianName,
            onValueChange = { guardianName = it },
            label = { Text("Guardian Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = guardianPhone,
            onValueChange = { guardianPhone = it },
            label = { Text("Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = guardianRelation,
            onValueChange = { guardianRelation = it },
            label = { Text("Relation") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {

                val guardian = Guardian(
                    name = guardianName,
                    phone = guardianPhone,
                    relation = guardianRelation
                )

                db.collection("users")
                    .document(uid)
                    .collection("contacts")
                    .add(guardian)
                    .addOnSuccessListener {

                        guardians = guardians + guardian

                        guardianName = ""
                        guardianPhone = ""
                        guardianRelation = ""
                    }

            }
        ) {
            Text("Add Guardian")
        }

        LazyColumn {

            items(guardians) { guardian ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = guardian.name,
                            fontWeight = FontWeight.Bold
                        )

                        Text(guardian.phone)

                        Text(guardian.relation)
                    }

                    IconButton(
                        onClick = {

                            val uid = auth.currentUser?.uid ?: return@IconButton

                            db.collection("users")
                                .document(uid)
                                .collection("contacts")
                                .whereEqualTo("phone", guardian.phone)
                                .get()
                                .addOnSuccessListener { result ->

                                    for (document in result.documents) {

                                        db.collection("users")
                                            .document(uid)
                                            .collection("contacts")
                                            .document(document.id)
                                            .delete()
                                    }

                                    guardians = guardians.filter {
                                        it.phone != guardian.phone
                                    }
                                }
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Guardian",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}