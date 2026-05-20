package com.example.womensafetyapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object LiveTrackingManager {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    @SuppressLint("MissingPermission")
    fun startTracking(

        context: Context

    ) {

        val uid =
            auth.currentUser?.uid
                ?: return

        val userName =
            auth.currentUser?.displayName
                ?: "User"

        val fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(context)

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000 // every 5 sec
            ).apply {

                setMinUpdateIntervalMillis(3000)

            }.build()

        val locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    val location: Location =
                        result.lastLocation
                            ?: return

                    uploadLocation(
                        uid,
                        userName,
                        location
                    )
                }
            }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun uploadLocation(

        uid: String,

        userName: String,

        location: Location

    ) {

        val data =
            hashMapOf(

                "latitude" to
                        location.latitude,

                "longitude" to
                        location.longitude,

                "timestamp" to
                        System.currentTimeMillis(),

                "username" to
                        userName
            )

        firestore.collection(
            "live_tracking"
        )
            .document(uid)
            .set(data)
    }
}