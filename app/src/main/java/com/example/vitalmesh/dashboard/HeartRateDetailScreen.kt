// ui/dashboard/HeartRateDetailScreen.kt
package com.example.vitalmesh.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vitalmesh.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val ecgData by viewModel.ecgData.collectAsState()
    val currentHeartRate = ecgData?.heart_rate_bpm ?: 0

    val heartRateColor = when {
        currentHeartRate == 0 -> Color.Gray
        currentHeartRate < 50 -> Color.Yellow
        currentHeartRate > 120 -> Color.Red
        else -> Color.Green
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
                    text = "Heart Rate Details",
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
                text = "Heart Monitoring",
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
                        text = "Current BPM: ${if (currentHeartRate > 0) currentHeartRate else "--"}",
                        color = heartRateColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HeartRateInfoRow("Minimum (today):", "62 BPM")
                    HeartRateInfoRow("Maximum (today):", "92 BPM")
                    HeartRateInfoRow("Average (today):", "74 BPM")
                    HeartRateInfoRow("Status:", "Normal")
                    HeartRateInfoRow("Heart Zone:", "Zone 2 (Aerobic)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Chart placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BPM Chart (Last 24h)",
                        color = colorResource(id = R.color.military_khaki),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HeartRateInfoRow(label: String, value: String) {
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
