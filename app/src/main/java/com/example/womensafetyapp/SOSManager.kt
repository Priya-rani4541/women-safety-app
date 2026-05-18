package com.example.womensafetyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
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
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    val mapsLink =
                        "https://maps.google.com/?q=$latitude,$longitude"

                    val message =
                        """
🚨 HELP! I am in danger.

📍 My Live Location:
$mapsLink
                        """.trimIndent()

                    // =========================
                    // SEND SMS TO CONTACTS
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
                                Log.d("SOS_DEBUG", "Preparing to send SMS")
                                val smsManager =
                                    SmsManager.getDefault()

                                for (document in result.documents) {

                                    val phone =
                                        document.getString("phone")

                                    if (!phone.isNullOrEmpty()) {
                                        Log.d(
                                            "SOS_DEBUG",
                                            "Sending SMS to: $phone"
                                        )
                                        smsManager.sendTextMessage(
                                            phone,
                                            null,
                                            message,
                                            null,
                                            null
                                        )
                                        Log.d(
                                            "SOS_DEBUG",
                                            "SMS SENT SUCCESSFULLY"
                                        )
                                    }
                                }

                                Toast.makeText(
                                    context,
                                    "SOS SMS Sent Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    // =========================
                    // SHARE AUDIO RECORDING
                    // =========================

                    val audioFile: File? =
                        audioRecorder.getRecordingFile()

                    if (audioFile != null && audioFile.exists()) {

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            audioFile
                        )

                        val shareIntent =
                            Intent(Intent.ACTION_SEND)

                        shareIntent.type = "audio/*"

                        shareIntent.putExtra(
                            Intent.EXTRA_STREAM,
                            uri
                        )

                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            """
🚨 HELP! I am in danger.

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
                    }

                    // =========================
                    // OPEN NEARBY POLICE
                    // =========================

                    val policeIntent = Intent(
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

                } else {

                    Toast.makeText(
                        context,
                        "Unable to get location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}