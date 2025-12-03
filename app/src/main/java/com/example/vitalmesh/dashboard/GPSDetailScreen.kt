package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vitalmesh.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPSDetailScreen(
    onBackClick: () -> Unit,
    onRequestFullscreenMap: () -> Unit, // ✅ NUEVO: Callback para navegación a tab GPS
    viewModel: SensorViewModel = viewModel()
) {
    val gpsData by viewModel.gpsData.collectAsState()

    val latitudeText = gpsData?.latitude?.let { "%.3f".format(it) } ?: "--"
    val longitudeText = gpsData?.longitude?.let { "%.3f".format(it) } ?: "--"
    val altitudeText = gpsData?.alt?.let { "%.1f".format(it) } ?: "--"

    val latitude = gpsData?.latitude
    val longitude = gpsData?.longitude
    val latLng = if (latitude != null && longitude != null)
        LatLng(latitude, longitude)
    else
        LatLng(40.713, -74.006)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

    // ✅ NUEVO: Actualizar posición del mapa cada 3 segundos
    LaunchedEffect(latLng) {
        while (true) {
            delay(3000) // 3 segundos
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.military_olive))
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.military_green)
            ),
            title = {
                Text(
                    text = "GPS Details",
                    color = colorResource(id = R.color.military_khaki),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colorResource(id = R.color.military_khaki)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Real-Time Location",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.military_khaki)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GPSInfoRow("Latitude:", latitudeText)
                    GPSInfoRow("Longitude:", longitudeText)
                    GPSInfoRow("Altitude:", "$altitudeText m")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ NUEVO: Mapa pequeño, clickable, se actualiza cada 3s
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onRequestFullscreenMap() }, // ← Click cambia a tab GPS
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = rememberMarkerState(position = latLng),
                            title = "📍 Ubicación actual"
                        )
                    }
                }
            }

            // Hint para el usuario
            Text(
                text = "👆 Tap the map to view full GPS screen",
                fontSize = 12.sp,
                color = colorResource(id = R.color.military_khaki).copy(alpha = 0.6f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun GPSInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Text(
            text = value,
            color = colorResource(id = R.color.military_khaki),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
