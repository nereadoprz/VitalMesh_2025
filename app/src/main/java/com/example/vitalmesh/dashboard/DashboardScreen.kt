// ui/dashboard/DashboardScreen.kt
package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vitalmesh.R
import kotlin.math.sqrt

// Enums and data classes
enum class SensorStatus {
    NORMAL, WARNING, CRITICAL, OFFLINE
}

data class SensorData(
    val name: String,
    val icon: ImageVector,
    val currentValue: String,
    val unit: String,
    val status: SensorStatus,
    val description: String
)

@Composable
fun DashboardScreen(
    onSensorClick: (String) -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val dht22Data by viewModel.dht22Data.collectAsState()
    val gsrData by viewModel.gsrData.collectAsState()
    val imuData by viewModel.imuData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Calculate IMU magnitudes
    val imuMagnitude = imuData?.let {
        sqrt(it.accel_g.x * it.accel_g.x +
                it.accel_g.y * it.accel_g.y +
                it.accel_g.z * it.accel_g.z)
    } ?: 0.0

    val gyroMagnitude = imuData?.let {
        sqrt(it.gyro_deg_s.x * it.gyro_deg_s.x +
                it.gyro_deg_s.y * it.gyro_deg_s.y +
                it.gyro_deg_s.z * it.gyro_deg_s.z)
    } ?: 0.0

    val movementState = when {
        imuData == null -> "Offline"
        imuMagnitude < 0.2 -> "⚠️ Free Fall"
        imuMagnitude in 0.8..1.2 && gyroMagnitude < 10 -> "Stationary"
        gyroMagnitude > 50 -> "Rotating"
        imuMagnitude > 2.0 -> "Active"
        else -> "🚶 Moving"
    }

    // Create sensor list with real Firebase data
    val sensors = listOf(
        SensorData(
            name = "Heart Rate",
            icon = Icons.Filled.Favorite,
            currentValue = "72",  // Static
            unit = "BPM",
            status = SensorStatus.NORMAL,
            description = "Cardiovascular monitoring"
        ),
        SensorData(
            name = "IMU",
            icon = Icons.Filled.Explore,
            currentValue = movementState,
            unit = String.format("%.2f g", imuMagnitude),
            status = when {
                imuData == null -> SensorStatus.OFFLINE
                imuMagnitude < 0.2 -> SensorStatus.CRITICAL
                gyroMagnitude > 50 -> SensorStatus.WARNING
                else -> SensorStatus.NORMAL
            },
            description = "Motion & orientation"
        ),
        SensorData(
            name = "GSR",
            icon = Icons.Filled.BatteryChargingFull,
            currentValue = String.format("%.0f", gsrData?.stress_level_0_100 ?: 0.0),
            unit = "Stress Level",
            status = when {
                gsrData == null -> SensorStatus.OFFLINE
                (gsrData?.stress_level_0_100 ?: 0.0) > 70 -> SensorStatus.CRITICAL
                (gsrData?.stress_level_0_100 ?: 0.0) > 50 -> SensorStatus.WARNING
                else -> SensorStatus.NORMAL
            },
            description = "Stress monitoring"
        ),
        SensorData(
            name = "GPS",
            icon = Icons.Filled.LocationOn,
            currentValue = "--",
            unit = "Not available",
            status = SensorStatus.OFFLINE,
            description = "Location tracking"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.military_green))
            .padding(16.dp)
    ) {
        Text(
            text = "Sensor Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.military_khaki),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SensorCard(
                    sensor = sensors[3], // GPS
                    onClick = { onSensorClick("GPS") },
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    sensor = sensors[1], // IMU
                    onClick = { onSensorClick("IMU") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SensorCard(
                    sensor = sensors[2], // GSR
                    onClick = { onSensorClick("GSR") },
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    sensor = sensors[0], // Heart Rate
                    onClick = { onSensorClick("HeartRate") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            StatusSummaryCard(sensors)
        }
    }
}

@Composable
fun SensorCard(
    sensor: SensorData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.card_background)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = sensor.icon,
                    contentDescription = sensor.name,
                    tint = colorResource(id = R.color.military_khaki),
                    modifier = Modifier.size(32.dp)
                )
                StatusIndicator(status = sensor.status)
            }

            Column {
                Text(
                    text = sensor.currentValue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.military_khaki)
                )
                Text(
                    text = sensor.unit,
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                )
            }

            Text(
                text = sensor.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.military_khaki)
            )
        }
    }
}

@Composable
fun StatusIndicator(status: SensorStatus) {
    val color = when (status) {
        SensorStatus.NORMAL -> Color.Green
        SensorStatus.WARNING -> Color.Yellow
        SensorStatus.CRITICAL -> Color.Red
        SensorStatus.OFFLINE -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, shape = RoundedCornerShape(6.dp))
    )
}

@Composable
fun StatusSummaryCard(sensors: List<SensorData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.card_background)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "System Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.military_khaki),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            sensors.forEach { sensor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusIndicator(status = sensor.status)
                        Text(
                            text = sensor.name,
                            color = colorResource(id = R.color.military_khaki)
                        )
                    }
                    Text(
                        text = when (sensor.status) {
                            SensorStatus.NORMAL -> "Normal"
                            SensorStatus.WARNING -> "Warning"
                            SensorStatus.CRITICAL -> "Critical"
                            SensorStatus.OFFLINE -> "Offline"
                        },
                        color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
