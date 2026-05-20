package com.example.womensafetyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.womensafetyapp.utils.AudioRecorder
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

object SOSManager {

    @SuppressLint("MissingPermission")
    fun sendSOS(

        context: Context,

        userId: String,

        audioRecorder: AudioRecorder

    ) {

        val fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val latitude =
                        location.latitude

                    val longitude =
                        location.longitude

                    // LOCATION LOG
                    Log.d(
                        "SOS_LOCATION",
                        "Latitude: $latitude Longitude: $longitude"
                    )

                    val mapsLink =
                        "https://maps.google.com/?q=$latitude,$longitude"

                    val message =
                        """
🚨 EMERGENCY ALERT 🚨

I need help immediately.

📍 Live Location:
$mapsLink

Sent from SheShield Safety App
                        """.trimIndent()

                    // =========================
                    // FETCH GUARDIAN CONTACTS
                    // =========================

                    FirebaseFirestore.getInstance()
                        .collection("emergency_contacts")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { result ->

                            if (
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.SEND_SMS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                val smsManager =

                                    if (
                                        Build.VERSION.SDK_INT >=
                                        Build.VERSION_CODES.S
                                    ) {

                                        context.getSystemService(
                                            SmsManager::class.java
                                        )

                                    } else {

                                        SmsManager.getDefault()
                                    }

                                for (document in result.documents) {

                                    val phone =
                                        document.getString("phone")

                                    if (!phone.isNullOrEmpty()) {

                                        try {

                                            // BEFORE SMS SEND
                                            Log.d(
                                                "SOS_SMS",
                                                "Sending SMS to: $phone"
                                            )

                                            smsManager.sendTextMessage(
                                                phone,
                                                null,
                                                message,
                                                null,
                                                null
                                            )

                                            // AFTER SMS SEND
                                            Log.d(
                                                "SOS_SMS",
                                                "SMS SENT SUCCESSFULLY"
                                            )

                                        } catch (e: Exception) {

                                            Log.e(
                                                "SOS_SMS",
                                                "SMS FAILED",
                                                e
                                            )
                                        }
                                    }
                                }

                                Toast.makeText(
                                    context,
                                    "Emergency SMS Sent",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    // =========================
                    // SHARE AUDIO RECORDING
                    // =========================

                    val audioFile: File? =
                        audioRecorder.getRecordingFile()

                    if (
                        audioFile != null &&
                        audioFile.exists()
                    ) {

                        try {

                            val uri =
                                FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    audioFile
                                )

                            val shareIntent =
                                Intent(Intent.ACTION_SEND)

                            shareIntent.type =
                                "audio/*"

                            shareIntent.putExtra(
                                Intent.EXTRA_STREAM,
                                uri
                            )

                            shareIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                """
🚨 EMERGENCY ALERT 🚨

📍 Live Location:
$mapsLink
                                """.trimIndent()
                            )

                            shareIntent.addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )

                            shareIntent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            )

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Send SOS Audio"
                                )
                            )

                        } catch (e: Exception) {

                            Log.e(
                                "SOS_DEBUG",
                                "Audio share failed",
                                e
                            )
                        }
                    }

                    // =========================
                    // OPEN NEARBY POLICE
                    // =========================

                    try {

                        val policeIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "geo:$latitude,$longitude?q=police station"
                                )
                            )

                        policeIntent.setPackage(
                            "com.google.android.apps.maps"
                        )

                        policeIntent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )

                        context.startActivity(policeIntent)

                    } catch (e: Exception) {

                        Log.e(
                            "SOS_DEBUG",
                            "Google Maps failed",
                            e
                        )
                    }

                } else {

                    Toast.makeText(
                        context,
                        "Unable to fetch location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}