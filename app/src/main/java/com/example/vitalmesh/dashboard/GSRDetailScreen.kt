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
fun GSRDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val gsrData by viewModel.gsrData.collectAsState()

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
                    text = "GSR Details",
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
                text = "Stress Level Monitoring",
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
                    GSRInfoRow("Conductance:", "${gsrData?.conductance_uS ?: "--"} μS")
                    GSRInfoRow("Resistance:", "${gsrData?.resistance_kOhm ?: "--"} kΩ")
                    GSRInfoRow("Stress Level:", "${gsrData?.stress_level_0_100?.toInt() ?: "--"} / 100")

                    val stressLevel = gsrData?.stress_level_0_100 ?: 0.0
                    val stressStatus = when {
                        stressLevel > 70 -> "High ⚠️"
                        stressLevel > 50 -> "Moderate ⚠️"
                        stressLevel > 30 -> "Low ✅"
                        else -> "Very Low ✅"
                    }
                    GSRInfoRow("Status:", stressStatus)
                }
            }
        }
    }
}

@Composable
fun GSRInfoRow(label: String, value: String) {
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
