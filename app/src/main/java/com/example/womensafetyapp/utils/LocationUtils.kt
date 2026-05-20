package com.example.womensafetyapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.*

object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(

        context: Context,

        onLocationReceived: (Double, Double) -> Unit

    ) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location: Location? ->

            location?.let {

                onLocationReceived(
                    it.latitude,
                    it.longitude
                )

            } ?: run {

                Toast.makeText(
                    context,
                    "Turn on GPS",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}