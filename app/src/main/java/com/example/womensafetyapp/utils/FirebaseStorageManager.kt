package com.example.womensafetyapp.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID

object FirebaseStorageManager {

    private val storage =
        FirebaseStorage.getInstance()

    fun uploadAudio(
        file: File,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val fileName =
            "sos_audio/${UUID.randomUUID()}.mp3"

        val ref =
            storage.reference.child(fileName)

        ref.putFile(Uri.fromFile(file))
            .continueWithTask { task ->

                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }

                ref.downloadUrl
            }
            .addOnSuccessListener {

                onSuccess(it.toString())
            }
            .addOnFailureListener {

                onFailure(it)
            }
    }
}