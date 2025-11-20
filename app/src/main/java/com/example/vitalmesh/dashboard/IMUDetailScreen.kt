package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vitalmesh.R
import kotlin.math.abs
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMUDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val imuData by viewModel.imuData.collectAsState()

    // Calculate magnitudes
    val accelMagnitude = imuData?.let {
        sqrt(it.accel_g.x * it.accel_g.x +
                it.accel_g.y * it.accel_g.y +
                it.accel_g.z * it.accel_g.z)
    } ?: 0.0

    val gyroMagnitude = imuData?.let {
        sqrt(it.gyro_deg_s.x * it.gyro_deg_s.x +
                it.gyro_deg_s.y * it.gyro_deg_s.y +
                it.gyro_deg_s.z * it.gyro_deg_s.z)
    } ?: 0.0

    // Use centralized logic from ViewModel
    val movementState = viewModel.determineMovementState(accelMagnitude, gyroMagnitude)
    val movementColor = viewModel.determineMovementColor(accelMagnitude, gyroMagnitude)

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
                    text = "IMU Sensor",
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
            // Movement state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Movement Status",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(movementColor)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movementState,
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Accelerometer - Visual
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Accelerometer",
                            color = colorResource(id = R.color.military_khaki),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            String.format("%.2f g", accelMagnitude),
                            color = colorResource(id = R.color.military_khaki),
                            fontSize = 16.sp
                        )
                    }

                    AccelBar("X", imuData?.accel_g?.x ?: 0.0, Color(0xFF4CAF50))
                    AccelBar("Y", imuData?.accel_g?.y ?: 0.0, Color(0xFF2196F3))
                    AccelBar("Z", imuData?.accel_g?.z ?: 0.0, Color(0xFFFF9800))
                }
            }

            // Gyroscope - Visual
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Gyroscope",
                            color = colorResource(id = R.color.military_khaki),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            String.format("%.1f °/s", gyroMagnitude),
                            color = colorResource(id = R.color.military_khaki),
                            fontSize = 16.sp
                        )
                    }

                    GyroBar("X (Pitch)", imuData?.gyro_deg_s?.x ?: 0.0, Color(0xFFE91E63))
                    GyroBar("Y (Roll)", imuData?.gyro_deg_s?.y ?: 0.0, Color(0xFF9C27B0))
                    GyroBar("Z (Yaw)", imuData?.gyro_deg_s?.z ?: 0.0, Color(0xFF673AB7))
                }
            }

            // Additional info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Info:",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "• Accelerometer: measures acceleration + gravity component (~1g at rest)\n" +
                                "• Gyroscope: measures rotational velocity (°/s)\n" +
                                "• At rest: accel ≈ 1g, gyro ≈ 0°/s\n" +
                                "• Free fall: accel < 0.5g (rare event)\n" +
                                "• Tilt affects the 1g reading based on device orientation",
                        color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AccelBar(axis: String, value: Double, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = axis,
                color = colorResource(id = R.color.military_khaki),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = String.format("%.2f g", value),
                color = colorResource(id = R.color.military_khaki),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            val normalizedValue = (abs(value) / 2.0).coerceIn(0.0, 1.0)
            Box(
                modifier = Modifier
                    .fillMaxWidth(normalizedValue.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun GyroBar(axis: String, value: Double, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = axis,
                color = colorResource(id = R.color.military_khaki),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = String.format("%.2f °/s", value),
                color = colorResource(id = R.color.military_khaki),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            val normalizedValue = (abs(value) / 100.0).coerceIn(0.0, 1.0)
            Box(
                modifier = Modifier
                    .fillMaxWidth(normalizedValue.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
