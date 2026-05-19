package com.example.womensafetyapp.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    var currentLocation by remember {
        mutableStateOf(
            LatLng(28.6139, 77.2090)
        )
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                currentLocation,
                16f
            )
        }

    LaunchedEffect(Unit) {

        startLocationUpdates(
            fusedLocationClient
        ) { location ->

            currentLocation = LatLng(
                location.latitude,
                location.longitude
            )

            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(
                    currentLocation,
                    17f
                )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true
            )
        ){

            Marker(
                state = MarkerState(
                    position = currentLocation
                ),
                title = "Live Location"
            )
        }

        FloatingActionButton(
            onClick = {
                onBack()
            }
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }
    }
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (Location) -> Unit
) {

    val locationRequest =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        )
            .setMinUpdateIntervalMillis(3000)
            .build()

    val locationCallback =
        object : LocationCallback() {

            override fun onLocationResult(
                locationResult: LocationResult
            ) {

                for (location in locationResult.locations) {

                    onLocationUpdate(location)
                }
            }
        }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}