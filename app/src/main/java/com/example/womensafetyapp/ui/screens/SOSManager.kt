package com.example.womensafetyapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object SOSManager {

    @SuppressLint("MissingPermission")
    fun sendSOS(context: Context) {

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid ?: return

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val sosData = hashMapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "timestamp" to System.currentTimeMillis(),
                        "status" to "ACTIVE"
                    )

                    db.collection("sos_alerts")
                        .document(uid)
                        .set(sosData)
                        .addOnSuccessListener {

                            Toast.makeText(
                                context,
                                "SOS Alert Sent!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }
}