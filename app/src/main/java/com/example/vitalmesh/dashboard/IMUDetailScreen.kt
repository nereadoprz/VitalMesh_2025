// ui/dashboard/IMUDetailScreen.kt
package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitalmesh.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMUDetailScreen(onBackClick: () -> Unit) {
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
                        "Accelerometer",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    IMUInfoRow("X:", "0.12 m/s²")
                    IMUInfoRow("Y:", "-0.05 m/s²")
                    IMUInfoRow("Z:", "9.81 m/s²")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Gyroscope",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    IMUInfoRow("Roll:", "2.3°")
                    IMUInfoRow("Pitch:", "-1.8°")
                    IMUInfoRow("Yaw:", "45.2°")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Magnetometer",
                        color = colorResource(id = R.color.military_khaki),
                        fontWeight = FontWeight.Bold
                    )
                    IMUInfoRow("Heading:", "287° (W)")
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
