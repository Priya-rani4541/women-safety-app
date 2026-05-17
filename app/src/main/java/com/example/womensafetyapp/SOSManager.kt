package com.example.womensafetyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

object SOSManager {

    @SuppressLint("MissingPermission")
    fun sendSOS(context: Context) {

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
HELP! I am in danger.

My live location:
$mapsLink
                        """.trimIndent()

                    val emergencyContacts = listOf(
                        "9876543210",
                        "9876543211"
                    )

                    if (
                        ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.SEND_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        val smsManager =
                            SmsManager.getDefault()

                        emergencyContacts.forEach {

                            smsManager.sendTextMessage(
                                it,
                                null,
                                message,
                                null,
                                null
                            )
                        }

                        Toast.makeText(
                            context,
                            "SOS Sent Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // OPEN NEARBY POLICE STATION
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