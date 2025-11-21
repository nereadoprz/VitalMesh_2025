package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSensorClick: (String) -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val dht22Data by viewModel.dht22Data.collectAsState()
    val ecgData by viewModel.ecgData.collectAsState()
    val gpsData by viewModel.gpsData.collectAsState()
    val gsrData by viewModel.gsrData.collectAsState()
    val imuData by viewModel.imuData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val imuMagnitude = imuData?.let {
        sqrt(
            it.accel_g.x * it.accel_g.x +
                    it.accel_g.y * it.accel_g.y +
                    it.accel_g.z * it.accel_g.z
        )
    } ?: 0.0

    val gyroMagnitude = imuData?.let {
        sqrt(
            it.gyro_deg_s.x * it.gyro_deg_s.x +
                    it.gyro_deg_s.y * it.gyro_deg_s.y +
                    it.gyro_deg_s.z * it.gyro_deg_s.z
        )
    } ?: 0.0

    val movementState = when {
        imuData == null -> "Offline"
        else -> viewModel.determineMovementState(imuMagnitude, gyroMagnitude)
    }

    val imuStatus = when {
        imuData == null -> SensorStatus.OFFLINE
        else -> viewModel.determineMovementStatus(imuMagnitude, gyroMagnitude)
    }

    val gpsCurrentValue = if (gpsData != null) {
        "%.3f, %.3f".format(gpsData!!.latitude, gpsData!!.longitude)
    } else {
        "--"
    }
    val gpsUnit = if (gpsData != null) "Lat, Lon" else "Not available"
    val gpsStatus = if (gpsData != null) SensorStatus.NORMAL else SensorStatus.OFFLINE

    val sensors = listOf(
        SensorData(
            name = "Heart Rate",
            icon = Icons.Filled.Favorite,
            currentValue = "${ecgData?.heart_rate_bpm ?: "--"}",
            unit = "BPM",
            status = when {
                ecgData == null -> SensorStatus.OFFLINE
                ecgData!!.heart_rate_bpm < 50 || ecgData!!.heart_rate_bpm > 120 -> SensorStatus.WARNING
                else -> SensorStatus.NORMAL
            },
            description = "Cardiovascular monitoring"
        ),
        SensorData(
            name = "IMU",
            icon = Icons.Filled.Explore,
            currentValue = movementState,
            unit = String.format("%.2f g", imuMagnitude),
            status = imuStatus,
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
            currentValue = gpsCurrentValue,
            unit = gpsUnit,
            status = gpsStatus,
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
                    modifier = Modifier.weight(1f),
                    isUpdating = isUpdating
                )
                SensorCard(
                    sensor = sensors[1], // IMU
                    onClick = { onSensorClick("IMU") },
                    modifier = Modifier.weight(1f),
                    isUpdating = isUpdating
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SensorCard(
                    sensor = sensors[2], // GSR
                    onClick = { onSensorClick("GSR") },
                    modifier = Modifier.weight(1f),
                    isUpdating = isUpdating
                )
                SensorCard(
                    sensor = sensors[0], // Heart Rate
                    onClick = { onSensorClick("HeartRate") },
                    modifier = Modifier.weight(1f),
                    isUpdating = isUpdating
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
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false
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
                // LED azul brillante pequeño a la izquierda del status, con separación sutil
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniBlueUpdateIndicator(isUpdating = isUpdating)
                    Spacer(modifier = Modifier.width(5.dp))
                    StatusIndicator(status = sensor.status)
                }
            }

            Column {
                Text(
                    text = sensor.currentValue,
                    fontSize = 20.sp,
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

// 🔵 LED azul brillante, MUY pequeño (como antes), a la izquierda del indicador de status
@Composable
fun MiniBlueUpdateIndicator(isUpdating: Boolean) {
    val color = if (isUpdating) Color(0xFF00B4FF) else Color.Transparent
    Box(
        modifier = Modifier
            .size(4.dp)
            .background(color, CircleShape)
    )
}

@Composable
fun StatusSummaryCard(sensors: List<SensorData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.card_background)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    Text(
                        text = sensor.name,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.military_khaki)
                    )
                    StatusIndicator(status = sensor.status)
                }
            }
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
            .background(color, shape = CircleShape)
    )
}
