package com.example.womensafetyapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.womensafetyapp.data.model.Guardian

// ── Colors (same as HomeScreen) ───────────────────────────────────────────────
private val EBg           = Color(0xFF0F0820)
private val ECardDark     = Color(0xFF1A0F2E)
private val EPurple       = Color(0xFF9333EA)
private val EPink         = Color(0xFFE8325A)
private val EWhite        = Color(0xFFFFFFFF)
private val EGray         = Color(0xFF9B8BB0)
private val EGreen        = Color(0xFF22C55E)
private val EGold         = Color(0xFFF4B942)
private val ENavInactive  = Color(0xFF5A4A6A)

@Composable
fun EmergencyContactScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    if (uid == null) {
        Text("User not logged in")
        return
    }

    var guardianName     by remember { mutableStateOf("") }
    var guardianPhone    by remember { mutableStateOf("") }
    var guardianRelation by remember { mutableStateOf("") }
    var guardians        by remember { mutableStateOf(listOf<Guardian>()) }
    var isAdding         by remember { mutableStateOf(false) }
    var isSaving         by remember { mutableStateOf(false) }


    // ── Load guardians ─────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        db.collection("users").document(uid).collection("contacts").get()
            .addOnSuccessListener { result ->
                guardians = result.documents.mapNotNull { it.toObject(Guardian::class.java) }
            }
            .addOnFailureListener {
                println(it.message)
            }
    }

    // ── Relation → Icon + Color ────────────────────────────────────────────────
    fun relationIcon(relation: String): ImageVector = when (relation.lowercase()) {
        "mother", "mom"          -> Icons.Default.Favorite
        "father", "dad"          -> Icons.Default.Person
        "brother", "sister"      -> Icons.Default.People
        "friend"                 -> Icons.Default.EmojiPeople
        "husband", "wife"        -> Icons.Default.Favorite
        else                     -> Icons.Default.Person
    }
    fun relationColor(relation: String): Color = when (relation.lowercase()) {
        "mother", "mom"          -> Color(0xFFE91E63)
        "father", "dad"          -> Color(0xFF2196F3)
        "brother"                -> Color(0xFF9333EA)
        "sister"                 -> Color(0xFFFF9800)
        "friend"                 -> EGreen
        "husband", "wife"        -> EPink
        else                     -> EGold
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EBg)
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(bottom = 32.dp)
        ) {

            // ── Top Bar ───────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A0E6B), EBg)
                            )
                        )
                        .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 24.dp)
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EWhite.copy(alpha = 0.08f))
                            .clickable { onBack() }
                            .align(Alignment.CenterStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint               = EWhite,
                            modifier           = Modifier.size(18.dp)
                        )
                    }

                    // Title
                    Column(
                        modifier            = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "My Guardians 🛡️",
                            color      = EWhite,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Trusted people for emergencies",
                            color    = EGray,
                            fontSize = 12.sp
                        )
                    }

                    // Guardian count badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "${guardians.size} added",
                            color      = EPurple,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Add Guardian Card ─────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF1E0A3B), ECardDark))
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(EPurple.copy(0.4f), EPink.copy(0.2f))),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        // Card header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier         = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EPurple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint               = EPurple,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Add Guardian",
                                    color      = EWhite,
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "They'll be alerted in emergencies",
                                    color    = EGray,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            // Expand/collapse toggle
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isAdding) EPink.copy(0.15f)
                                        else EPurple.copy(0.15f)
                                    )
                                    .clickable { isAdding = !isAdding },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAdding) Icons.Default.Close
                                    else Icons.Default.Add,
                                    contentDescription = null,
                                    tint     = if (isAdding) EPink else EPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Expandable form
                        if (isAdding) {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = EWhite.copy(alpha = 0.06f))
                            Spacer(Modifier.height(16.dp))

                            // Name field
                            GuardianTextField(
                                value       = guardianName,
                                onValueChange = { guardianName = it },
                                label       = "Full Name",
                                icon        = Icons.Default.Person,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(Modifier.height(10.dp))

                            // Phone field
                            GuardianTextField(
                                value        = guardianPhone,
                                onValueChange = { guardianPhone = it },
                                label        = "Phone Number",
                                icon         = Icons.Default.Phone,
                                keyboardType = KeyboardType.Phone
                            )
                            Spacer(Modifier.height(10.dp))

                            // Relation field
                            GuardianTextField(
                                value        = guardianRelation,
                                onValueChange = { guardianRelation = it },
                                label        = "Relation (e.g. Mom, Brother)",
                                icon         = Icons.Default.Favorite,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(Modifier.height(16.dp))

                            // Save button
                            Button(
                                onClick = {
                                    val auth = FirebaseAuth.getInstance()
                                    val db = FirebaseFirestore.getInstance()

                                    val uid = auth.currentUser?.uid ?: return@Button

                                    val guardian = hashMapOf(
                                        "name" to guardianName,
                                        "phone" to guardianPhone,
                                        "relation" to guardianRelation
                                    )

                                    db.collection("users")
                                        .document(uid)
                                        .collection("contacts")
                                        .add(guardian)
                                        .addOnSuccessListener {

                                            Toast.makeText(
                                                context,
                                                "Guardian Added Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            guardianName = ""
                                            guardianPhone = ""
                                            guardianRelation = ""
                                        }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EPurple
                                ),
                                enabled = !isSaving
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        color    = EWhite,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Save Guardian",
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── Section Header ────────────────────────────────────────────────
            item {
                Row(
                    modifier              = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "SAVED GUARDIANS",
                        color         = EWhite.copy(alpha = 0.45f),
                        fontSize      = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight    = FontWeight.Bold,
                        fontFamily    = FontFamily.Monospace
                    )
                    if (guardians.isNotEmpty()) {
                        Text(
                            "${guardians.size} contact${if (guardians.size == 1) "" else "s"}",
                            color    = EPurple,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Empty State ───────────────────────────────────────────────────
            if (guardians.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No guardians added yet",
                            color      = EWhite.copy(alpha = 0.6f),
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Add trusted contacts above",
                            color    = EGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ── Guardian Cards ────────────────────────────────────────────────
            items(guardians) { guardian ->
                val accent = relationColor(guardian.relation)
                val icon   = relationIcon(guardian.relation)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1A0F2E), Color(0xFF130A20))
                            )
                        )
                        .border(
                            1.dp,
                            accent.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.15f))
                                .border(1.5.dp, accent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (guardian.name.isNotBlank())
                                    guardian.name[0].uppercase()
                                else
                                    "?",
                                color      = accent,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )


                        }

                        Spacer(Modifier.width(14.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                guardian.name,
                                color      = EWhite,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint     = EGray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    guardian.phone,
                                    color    = EGray,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint     = accent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    guardian.relation,
                                    color      = accent,
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Call button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EGreen.copy(alpha = 0.12f))
                                    .clickable { /* TODO: dial */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint     = EGreen,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EPink.copy(alpha = 0.12f))
                                    .clickable {
                                        db.collection("users").document(uid)
                                            .collection("contacts")
                                            .whereEqualTo("phone", guardian.phone)
                                            .get()
                                            .addOnSuccessListener { result ->
                                                for (doc in result.documents) {
                                                    db.collection("users").document(uid)
                                                        .collection("contacts")
                                                        .document(doc.id).delete()
                                                }
                                                guardians = guardians.filter {
                                                    it.phone != guardian.phone
                                                }
                                            }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint     = EPink,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Reusable styled text field ─────────────────────────────────────────────────
@Composable
private fun GuardianTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, color = EGray, fontSize = 12.sp) },
        leadingIcon   = {
            Icon(icon, contentDescription = null, tint = EPurple, modifier = Modifier.size(18.dp))
        },
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = EPurple,
            unfocusedBorderColor = EWhite.copy(alpha = 0.1f),
            focusedTextColor     = EWhite,
            unfocusedTextColor   = EWhite,
            cursorColor          = EPurple,
            focusedContainerColor   = EWhite.copy(alpha = 0.04f),
            unfocusedContainerColor = EWhite.copy(alpha = 0.02f)
        ),
        singleLine = true
    )
}