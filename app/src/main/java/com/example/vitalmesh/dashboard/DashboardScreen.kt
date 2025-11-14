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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitalmesh.R

data class SensorData(
    val name: String,
    val icon: ImageVector,
    val currentValue: String,
    val unit: String,
    val status: SensorStatus,
    val description: String
)

enum class SensorStatus {
    NORMAL, WARNING, CRITICAL, OFFLINE
}

@Composable
fun DashboardScreen(
    onSensorClick: (String) -> Unit
) {
    val sensors = listOf(
        SensorData(
            name = "GPS",
            icon = Icons.Filled.LocationOn,
            currentValue = "34.0522° N",
            unit = "118.2437° W",
            status = SensorStatus.NORMAL,
            description = "Location tracking"
        ),
        SensorData(
            name = "IMU",
            icon = Icons.Filled.Explore,
            currentValue = "45.2°",
            unit = "Orientation",
            status = SensorStatus.NORMAL,
            description = "Motion & orientation"
        ),
        SensorData(
            name = "GSR",
            icon = Icons.Filled.BatteryChargingFull,
            currentValue = "2.8",
            unit = "μS",
            status = SensorStatus.WARNING,
            description = "Stress level"
        ),
        SensorData(
            name = "Heart Rate",
            icon = Icons.Filled.Favorite,
            currentValue = "78",
            unit = "BPM",
            status = SensorStatus.NORMAL,
            description = "Cardiac monitoring"
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
                    sensor = sensors[0],
                    onClick = { onSensorClick("GPS") },
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    sensor = sensors[1],
                    onClick = { onSensorClick("IMU") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SensorCard(
                    sensor = sensors[2],
                    onClick = { onSensorClick("GSR") },
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    sensor = sensors[3],
                    onClick = { onSensorClick("HeartRate") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        StatusSummaryCard(sensors)
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
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = sensor.icon,
                    contentDescription = sensor.name,
                    tint = colorResource(id = R.color.military_khaki),
                    modifier = Modifier.size(32.dp)
                )
                StatusIndicator(sensor.status)
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
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                )
            }

            Text(
                text = sensor.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.military_khaki)
            )
        }
    }
}

@Composable
fun StatusIndicator(status: SensorStatus) {
    val colorId = when (status) {
        SensorStatus.NORMAL -> R.color.status_normal
        SensorStatus.WARNING -> R.color.status_warning
        SensorStatus.CRITICAL -> R.color.status_critical
        SensorStatus.OFFLINE -> R.color.status_offline
    }
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(colorResource(id = colorId), shape = RoundedCornerShape(6.dp))
    )
}

@Composable
fun StatusSummaryCard(sensors: List<SensorData>) {
    val normalCount = sensors.count { it.status == SensorStatus.NORMAL }
    val warningCount = sensors.count { it.status == SensorStatus.WARNING }
    val criticalCount = sensors.count { it.status == SensorStatus.CRITICAL }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "System Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.military_khaki)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusItem("Normal", normalCount, R.color.status_normal)
                StatusItem("Warning", warningCount, R.color.status_warning)
                StatusItem("Critical", criticalCount, R.color.status_critical)
            }
        }
    }
}

@Composable
fun StatusItem(label: String, count: Int, colorId: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(colorResource(id = colorId), shape = RoundedCornerShape(5.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $count",
            fontSize = 14.sp,
            color = colorResource(id = R.color.military_khaki)
        )
    }
}
