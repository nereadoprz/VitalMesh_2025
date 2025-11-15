package com.example.vitalmesh.gpsmap

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitalmesh.R
import com.example.vitalmesh.FirebaseLocationService
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun GPSScreen() {
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(40.7128, -74.0060), 15f)
    }

    LaunchedEffect(Unit) {
        FirebaseLocationService.getUserCurrentLocation { lat, lng ->
            currentLocation = LatLng(lat, lng)
            isLoading = false
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.military_olive))
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.85f),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { Log.d("GPSScreen", "Mapa cargado") }
        ) {
            currentLocation?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "📍 Ubicación actual"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isLoading) "⏳ Cargando ubicación..." else "Lat: ${currentLocation?.latitude} | Lng: ${currentLocation?.longitude}",
            color = colorResource(id = R.color.military_khaki),
            fontSize = 12.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
