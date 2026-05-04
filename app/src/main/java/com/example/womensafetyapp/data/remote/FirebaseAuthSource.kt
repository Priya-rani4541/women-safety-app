package com.example.womensafetyapp.data.remote

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthSource {

    private val auth = FirebaseAuth.getInstance()

    fun register(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)

    fun login(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    fun sendVerification() =
        auth.currentUser?.sendEmailVerification()

    fun isVerified(): Boolean =
        auth.currentUser?.isEmailVerified ?: false

    fun resetPassword(email: String) =
        auth.sendPasswordResetEmail(email)

    fun logout() = auth.signOut()
}