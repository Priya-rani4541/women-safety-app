package com.example.womensafetyapp.ui.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LiveTrackingScreen(

    userId: String

) {

    val firestore =
        FirebaseFirestore.getInstance()

    var latitude by remember {
        mutableDoubleStateOf(0.0)
    }

    var longitude by remember {
        mutableDoubleStateOf(0.0)
    }

    // REALTIME FIREBASE LISTENER
    LaunchedEffect(Unit) {

        firestore.collection("live_tracking")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->

                if (
                    snapshot != null &&
                    snapshot.exists()
                ) {

                    latitude =
                        snapshot.getDouble("latitude")
                            ?: 0.0

                    longitude =
                        snapshot.getDouble("longitude")
                            ?: 0.0
                }
            }
    }

    val currentLocation =
        LatLng(latitude, longitude)

    val cameraPositionState =
        rememberCameraPositionState {

            position =
                CameraPosition.fromLatLngZoom(
                    currentLocation,
                    16f
                )
        }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState =
            cameraPositionState
    ) {

        Marker(
            state =
                MarkerState(
                    position = currentLocation
                ),
            title = "Victim Live Location"
        )
    }
}