package com.example.womensafetyapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    // LOGIN
    suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            val user = auth.currentUser

            if (user?.isEmailVerified == true) {

                Result.success("Login Success")

            } else {

                auth.signOut()

                Result.failure(
                    Exception("Please verify your email")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // REGISTER
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<String> {

        return try {

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val user = auth.currentUser
                ?: return Result.failure(
                    Exception("User not found")
                )

            user.sendEmailVerification().await()

            saveUserToFirestore(
                uid = user.uid,
                name = name,
                email = email,
                provider = "email"
            )

            Result.success("Verification email sent")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // GOOGLE LOGIN
    suspend fun googleLogin(
        idToken: String
    ): Result<String> {

        return try {

            val credential =
                GoogleAuthProvider.getCredential(
                    idToken,
                    null
                )

            val result =
                auth.signInWithCredential(
                    credential
                ).await()

            val user = result.user
                ?: return Result.failure(
                    Exception("Google user not found")
                )

            saveUserToFirestore(
                uid = user.uid,
                name = user.displayName ?: "No Name",
                email = user.email ?: "",
                provider = "google"
            )

            Result.success("Google Sign-In Success")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // RESET PASSWORD
    suspend fun resetPassword(
        email: String
    ): Result<String> {

        return try {

            auth.sendPasswordResetEmail(email)
                .await()

            Result.success("Reset link sent")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // SAVE USER
    private suspend fun saveUserToFirestore(
        uid: String,
        name: String,
        email: String,
        provider: String
    ) {

        val userData = hashMapOf(

            "uid" to uid,

            "name" to name,

            "email" to email,

            "provider" to provider,

            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid)
            .set(userData, SetOptions.merge())
            .await()
    }

    // LOGOUT
    fun logout() {

        auth.signOut()
    }

    // CURRENT USER
    fun getCurrentUser() = auth.currentUser
}