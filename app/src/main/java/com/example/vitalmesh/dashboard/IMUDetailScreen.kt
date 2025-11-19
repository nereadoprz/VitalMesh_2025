package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vitalmesh.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMUDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val imuData by viewModel.imuData.collectAsState()

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
                    text = "IMU Details",
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
                text = "Movement and Orientation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.military_khaki)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Accelerometer (g)",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    IMUInfoRow("X:", String.format("%.2f g", imuData?.accel_g?.x ?: 0.0))
                    IMUInfoRow("Y:", String.format("%.2f g", imuData?.accel_g?.y ?: 0.0))
                    IMUInfoRow("Z:", String.format("%.2f g", imuData?.accel_g?.z ?: 0.0))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Gyroscope (deg/s)",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    IMUInfoRow("X:", String.format("%.2f °/s", imuData?.gyro_deg_s?.x ?: 0.0))
                    IMUInfoRow("Y:", String.format("%.2f °/s", imuData?.gyro_deg_s?.y ?: 0.0))
                    IMUInfoRow("Z:", String.format("%.2f °/s", imuData?.gyro_deg_s?.z ?: 0.0))

                    Spacer(modifier = Modifier.height(12.dp))

                    IMUInfoRow("Device ID:", imuData?.id ?: "--")
                    IMUInfoRow("Timestamp:", "${imuData?.timestamp_ms ?: "--"}")
                }
            }
        }
    }
}

@Composable
fun IMUInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = colorResource(id = R.color.military_khaki),
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = colorResource(id = R.color.military_khaki),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
