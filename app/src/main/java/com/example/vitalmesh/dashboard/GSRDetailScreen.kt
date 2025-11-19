package com.example.vitalmesh.dashboard

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val historicalData by viewModel.gsrHistoricalData.collectAsState()

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

            // Current data card
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

            // Historical chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.card_background)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📊 Stress History (Last ${historicalData.size} samples)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.military_khaki),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (historicalData.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading historical data...",
                                color = colorResource(id = R.color.military_khaki).copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        StressLevelChart(
                            data = historicalData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StressLevelChart(
    data: List<GSRHistoricalPoint>,
    modifier: Modifier = Modifier
) {
    val lineColor = Color(0xFF4CAF50)
    val criticalColor = Color(0xFFF44336)
    val warningColor = Color(0xFFFFC107)
    val gridColor = Color.Gray.copy(alpha = 0.3f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val padding = 40f
            val graphWidth = canvasWidth - padding * 2
            val graphHeight = canvasHeight - padding * 2

            // Draw grid lines (horizontal)
            for (i in 0..4) {
                val y = padding + (graphHeight / 4) * i
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(canvasWidth - padding, y),
                    strokeWidth = 1f
                )
            }

            // Draw threshold lines
            // Critical (70%)
            val criticalY = padding + graphHeight - (graphHeight * 0.7f)
            drawLine(
                color = criticalColor.copy(alpha = 0.3f),
                start = Offset(padding, criticalY),
                end = Offset(canvasWidth - padding, criticalY),
                strokeWidth = 2f
            )

            // Warning (50%)
            val warningY = padding + graphHeight - (graphHeight * 0.5f)
            drawLine(
                color = warningColor.copy(alpha = 0.3f),
                start = Offset(padding, warningY),
                end = Offset(canvasWidth - padding, warningY),
                strokeWidth = 2f
            )

            // Draw data line
            if (data.size > 1) {
                val path = Path()
                val pointSpacing = graphWidth / (data.size - 1).coerceAtLeast(1)

                data.forEachIndexed { index, point ->
                    val x = padding + pointSpacing * index
                    val normalizedValue = (point.stressLevel / 100.0).coerceIn(0.0, 1.0)
                    val y = padding + graphHeight - (graphHeight * normalizedValue).toFloat()

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                    // Draw point
                    drawCircle(
                        color = when {
                            point.stressLevel > 70 -> criticalColor
                            point.stressLevel > 50 -> warningColor
                            else -> lineColor
                        },
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }

                // Draw line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3f)
                )
            }
        }

        // Sample number labels
        if (data.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#${data.first().sampleNumber}",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                )
                Text(
                    text = "Sample Number",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                )
                Text(
                    text = "#${data.last().sampleNumber}",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                )
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
