package com.example.womensafetyapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.womensafetyapp.data.model.EmergencyContact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val firestore = FirebaseFirestore.getInstance()

    val currentUser =
        FirebaseAuth.getInstance().currentUser

    var contactName by remember {
        mutableStateOf("")
    }

    var contactPhone by remember {
        mutableStateOf("")
    }

    val contacts = remember {
        mutableStateListOf<EmergencyContact>()
    }

    // LOAD CONTACTS
    LaunchedEffect(Unit) {

        currentUser?.uid?.let { uid ->

            firestore.collection("emergency_contacts")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->

                    contacts.clear()

                    for (document in result) {

                        val contact =
                            document.toObject(
                                EmergencyContact::class.java
                            )

                        contacts.add(contact)
                    }
                }
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    )

    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF4B91),
            Color(0xFF9C27B0)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {

            // TOP BAR
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Emergency Contacts",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTACT NAME
            OutlinedTextField(
                value = contactName,
                onValueChange = {
                    contactName = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Contact Name")
                },
                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF4B91),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color(0xFFFF4B91),
                    cursorColor = Color(0xFFFF4B91),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PHONE NUMBER
            OutlinedTextField(
                value = contactPhone,
                onValueChange = {
                    contactPhone = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Phone Number")
                },
                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF4B91),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color(0xFFFF4B91),
                    cursorColor = Color(0xFFFF4B91),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SAVE BUTTON
            Button(
                onClick = {

                    currentUser?.uid?.let { uid ->

                        val contact = hashMapOf(

                            "userId" to uid,

                            "name" to contactName,

                            "phone" to contactPhone
                        )

                        firestore.collection("emergency_contacts")
                            .add(contact)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    context,
                                    "Contact Saved Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                contacts.add(
                                    EmergencyContact(
                                        uid,
                                        contactName,
                                        contactPhone
                                    )
                                )

                                contactName = ""
                                contactPhone = ""
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    context,
                                    "Failed: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = buttonGradient,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Save Contact",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Saved Contacts",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn {

                items(contacts) { contact ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF232946)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFFF4B91),
                                                Color(0xFF9C27B0)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {

                                Text(
                                    text = contact.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = contact.phone,
                                    color = Color.LightGray,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}