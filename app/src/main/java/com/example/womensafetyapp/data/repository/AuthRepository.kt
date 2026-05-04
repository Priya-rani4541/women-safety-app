package com.example.womensafetyapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 🔐 LOGIN
    suspend fun login(email: String, password: String): String {
        auth.signInWithEmailAndPassword(email, password).await()

        val user = auth.currentUser

        return if (user?.isEmailVerified == true) {
            "Login Success"
        } else {
            auth.signOut()
            "Please verify your email"
        }
    }

    // 📝 REGISTER (EMAIL)
    suspend fun register(name: String, email: String, password: String): String {

        auth.createUserWithEmailAndPassword(email, password).await()

        val user = auth.currentUser ?: return "User error"

        user.sendEmailVerification()

        saveUserToFirestore(
            uid = user.uid,
            name = name,
            email = email,
            provider = "email"
        )

        return "Verification email sent"
    }

    // 🔵 GOOGLE LOGIN
    suspend fun googleLogin(idToken: String): Boolean {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()

        val user = result.user ?: return false

        saveUserToFirestore(
            uid = user.uid,
            name = user.displayName ?: "No Name",
            email = user.email ?: "",
            provider = "google"
        )

        return true
    }

    // 🔁 RESET PASSWORD
    suspend fun resetPassword(email: String): String {
        auth.sendPasswordResetEmail(email).await()
        return "Reset link sent"
    }

    // 🔄 COMMON SAVE FUNCTION (IMPORTANT)
    private suspend fun saveUserToFirestore(
        uid: String,
        name: String,
        email: String,
        provider: String
    ) {
        val data = mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "provider" to provider,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid)
            .set(data, SetOptions.merge()) // ✅ FIXED
            .await()
    }

    // 🚪 LOGOUT
    fun logout() {
        auth.signOut()
    }
}