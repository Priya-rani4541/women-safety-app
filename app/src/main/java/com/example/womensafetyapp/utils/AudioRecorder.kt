package com.example.womensafetyapp.utils

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AudioRecorder(
    private val context: Context
) {

    private var recorder: MediaRecorder? = null

    private var audioFile: File? = null

    private var isRecording = false

    fun startRecording() {

        try {

            audioFile = File(
                context.getExternalFilesDir(null),
                "sos_recording.mp4"
            )

            recorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    MediaRecorder()
                }

            recorder?.apply {

                setAudioSource(MediaRecorder.AudioSource.MIC)

                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                setOutputFile(audioFile!!.absolutePath)

                prepare()

                start()
                isRecording = true

                Log.d(
                    "SOS_AUDIO",
                    "Recording Started"
                )

                // AUTO STOP AFTER 10 SEC
                Handler(Looper.getMainLooper()).postDelayed({

                    stopRecording()

                }, 10000)
            }

        } catch (e: Exception) {

            Log.e(
                "SOS_AUDIO",
                "Recording Failed",
                e
            )

            e.printStackTrace()
        }
    }

    fun stopRecording() {

        try {

            recorder?.apply {

                stop()

                release()
            }

            recorder = null
            uploadRecordingToFirebase { url ->

                Log.d(
                    "SOS_AUDIO",
                    "Uploaded: $url"
                )
            }
            isRecording = false

            Log.d(
                "SOS_AUDIO",
                "Recording Stopped"
            )

        } catch (e: Exception) {

            Log.e(
                "SOS_AUDIO",
                "Stop Failed",
                e
            )

            e.printStackTrace()
        }

    }
    fun uploadRecordingToFirebase(
        onUploaded: (String) -> Unit
    ) {

        val file = audioFile ?: return

        val storageRef =
            FirebaseStorage.getInstance()
                .reference
                .child("sos_recordings/${file.name}")

        storageRef.putFile(file.toURI().toString().toUri())
            .addOnSuccessListener {

                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->

                        val url = uri.toString()

                        // SAVE URL TO FIRESTORE
                        FirebaseFirestore.getInstance()
                            .collection("sos_evidence")
                            .add(
                                hashMapOf(
                                    "audioUrl" to url,
                                    "timestamp" to System.currentTimeMillis(),
                                    "userId" to FirebaseAuth
                                        .getInstance()
                                        .currentUser?.uid
                                )
                            )

                        val shareIntent = Intent().apply {

                            action = Intent.ACTION_SEND

                            putExtra(
                                Intent.EXTRA_TEXT,

                                """
                                    🚨 EMERGENCY ALERT 🚨
                                    
                                    Live Location:
                                    https://maps.google.com/?q=31.2240,75.7708
                                    
                                    Audio Evidence:
                                    $url
                                    """.trimIndent()
                            )

                            type = "text/plain"

                            `package` = "com.whatsapp"
                        }

                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        try {

                            context.startActivity(shareIntent)

                        } catch (e: Exception) {

                            Log.e(
                                "SOS_SHARE",
                                "WhatsApp not installed",
                                e
                            )
                        }

                        onUploaded(url)
                    }
            }
    }

    fun getRecordingFile(): File? {

        return audioFile
    }
    fun isCurrentlyRecording(): Boolean {

        return isRecording
    }
}
