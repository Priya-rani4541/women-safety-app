package com.example.womensafetyapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource {

    private val auth = FirebaseAuth.getInstance()

    // --------------------------------------------------
    // REGISTER
    // --------------------------------------------------

    suspend fun register(
        email: String,
        password: String
    ): Result<String> {

        return try {

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success("Registration Success")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success("Login Success")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // EMAIL VERIFICATION
    // --------------------------------------------------

    suspend fun sendVerification(): Result<String> {

        return try {

            auth.currentUser
                ?.sendEmailVerification()
                ?.await()

            Result.success("Verification Email Sent")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // CHECK EMAIL VERIFIED
    // --------------------------------------------------

    fun isVerified(): Boolean {

        return auth.currentUser?.isEmailVerified ?: false
    }

    // --------------------------------------------------
    // RESET PASSWORD
    // --------------------------------------------------

    suspend fun resetPassword(
        email: String
    ): Result<String> {

        return try {

            auth.sendPasswordResetEmail(email)
                .await()

            Result.success("Reset Email Sent")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // GOOGLE LOGIN
    // --------------------------------------------------

    suspend fun googleLogin(
        idToken: String
    ): Result<String> {

        return try {

            val credential =
                GoogleAuthProvider.getCredential(
                    idToken,
                    null
                )

            auth.signInWithCredential(credential)
                .await()

            Result.success("Google Login Success")

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // CURRENT USER
    // --------------------------------------------------

    fun currentUser() = auth.currentUser

    // --------------------------------------------------
    // LOGOUT
    // --------------------------------------------------

    fun logout() {

        auth.signOut()
    }
}